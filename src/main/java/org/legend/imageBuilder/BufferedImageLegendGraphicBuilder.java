package org.legend.imageBuilder;

/* (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.geotools.geometry.jts.LiteShape2;
import org.geotools.map.FeatureLayer;
import org.geotools.renderer.lite.MetaBufferEstimator;
import org.geotools.renderer.lite.StyledShapePainter;
import org.geotools.renderer.style.SLDStyleFactory;
import org.geotools.renderer.style.Style2D;
import org.geotools.styling.*;
import org.geotools.styling.visitor.RescaleStyleVisitor;
import org.geotools.util.NumberRange;
import org.legend.legendGraphicDetails.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.style.GraphicLegend;

/**
 * Template based on <a
 * href="http://svn.geotools.org/geotools/trunk/gt/module/main/src/org/geotools/renderer/lite/StyledShapePainter.java">
 * GeoTools StyledShapePainter</a> that produces a BufferedImage with the appropriate legend graphic
 * for a given GetLegendGraphic WMS request.
 *
 * <p>It should be enough for a subclass to implement and <code>
 * getContentType()</code> in order to encode the BufferedImage produced by this class to the
 * appropriate output format.
 *
 * <p>This class takes literally the fact that the arguments <code>WIDTH</code> and <code>HEIGHT
 * </code> are just <i>hints</i> about the desired dimensions of the produced graphic, and the need
 * to produce a legend graphic representative enough of the SLD style for which it is being
 * generated. Thus, if no <code>RULE</code> parameter was passed and the style has more than one
 * applicable Rule for the actual scale factor, there will be generated a legend graphic of the
 * specified width, but with as many stacked graphics as applicable rules were found, providing by
 * this way a representative enough legend.
 *
 * @author Gabriel Roldan
 * @author Simone Giannecchini, GeoSolutions SAS
 * @version $Id$
 */
public class BufferedImageLegendGraphicBuilder extends LegendGraphicBuilder {

    /**
     * Singleton shape painter to serve all legend requests. We can use a single shape painter
     * instance as long as it remains thread safe.
     */
    private static final StyledShapePainter shapePainter = new StyledShapePainter();

    /** used to create sample point shapes with LiteShape (not lines nor polygons) */
    private static final GeometryFactory geomFac = new GeometryFactory();

    /**
     * Just a holder to avoid creating many point shapes from inside <code>getSampleShape()</code>
     */
    private LiteShape2 samplePoint;

    /**
     * Default constructor. Subclasses may provide its own with a String parameter to establish its
     * desired output format, if they support more than one (e.g. a JAI based one)
     */
    public BufferedImageLegendGraphicBuilder() {
        super();
    }

    /**
     * Takes a GetLegendGraphicRequest and produces a BufferedImage that then can be used by a
     * subclass to encode it to the appropriate output format.
     *
     * @throws Exception if there are problems creating a "sample" feature instance for the
     *     FeatureType <code>request</code> returns as the required layer (which should not occur).
     */
    public BufferedImage buildLegendGraphic(List<FeatureLayer> featureLayerList, Map<String, Object> legendOptions) throws Exception {
        // list of images to be rendered for the layers (more than one if
        // a layer list is given)
        setup(legendOptions);

        List<RenderedImage> layersImages = new ArrayList<>();

        for (FeatureLayer featureLayer : featureLayerList) {

            FeatureType layer = featureLayer.getFeatureSource().getSchema();
            // style and rule to use for the current layer
            Style gt2Style = featureLayer.getStyle();
            if (gt2Style == null) {
                throw new NullPointerException("There is no style in featureLayer");
            }

            RenderedImage titleImage = null;

            Feature sampleFeature = createSampleFeature(layer);

            final FeatureTypeStyle[] ftStyles = gt2Style.featureTypeStyles().toArray(new FeatureTypeStyle[0]);
            final double scaleDenominator = -1.0;
            Rule[] applicableRules = LegendUtils.getApplicableRules(ftStyles, scaleDenominator);

            final SLDStyleFactory styleFactory = new SLDStyleFactory();

            /*
             * Default minimum size for symbols rendering. Can be overridden using LEGEND_OPTIONS
             * (minSymbolSize).
             */
            double minimumSymbolSize = 3.0;

            // calculate the symbols rescaling factor necessary for them to be
            // drawn inside the icon box
            int defaultSize = Math.min(w, h);

            double[] minMax =
                    calcSymbolSize(
                            defaultSize,
                            minimumSymbolSize,
                            layer,
                            sampleFeature,
                            applicableRules);
            double actualMin = minMax[0];
            double actualMax = minMax[1];
            boolean rescalingRequired =
                    actualMin < minimumSymbolSize || actualMax > defaultSize;
            java.util.function.Function<Double, Double> rescaler;
            if (actualMax == actualMin
                    || ((actualMin / actualMax) * defaultSize) > minimumSymbolSize) {
                rescaler = size -> (size / actualMax) * defaultSize;
            } else {
                rescaler =
                        size ->
                                (size - actualMin)
                                        / (actualMax - actualMin)
                                        * (defaultSize - minimumSymbolSize)
                                        + minimumSymbolSize;
            }
            boolean transparent = false;
            final NumberRange<Double> scaleRange = NumberRange.create(scaleDenominator, scaleDenominator);
            final int ruleCount = applicableRules.length;
            final List<RenderedImage> legendsStack = new ArrayList<>(ruleCount);
            renderRules(
                    legendOptions,
                    layersImages,
                    forceLabelsOn,
                    forceLabelsOff,
                    layer,
                    transparent,
                    titleImage,
                    sampleFeature,
                    applicableRules,
                    scaleRange,
                    ruleCount,
                    legendsStack,
                    styleFactory,
                    minimumSymbolSize,
                    rescalingRequired,
                    rescaler);
        }
        // all legend graphics are merged if we have a layer group
        BufferedImage finalLegend =
                mergeGroups(
                        layersImages, legendOptions, forceLabelsOn, forceLabelsOff);
        if (finalLegend == null) {
            throw new IllegalArgumentException("no legend passed");
        }
        return finalLegend;
    }

    /** */
    private void renderRules(
            Map<String, Object> legendOptions,
            List<RenderedImage> layersImages,
            boolean forceLabelsOn,
            boolean forceLabelsOff,
            FeatureType layer,
            final boolean transparent,
            RenderedImage titleImage,
            final Feature sampleFeature,
            Rule[] applicableRules,
            final NumberRange<Double> scaleRange,
            final int ruleCount,
            final List<RenderedImage> legendsStack,
            final SLDStyleFactory styleFactory,
            double minimumSymbolSize,
            boolean rescalingRequired,
            Function<Double, Double> rescaler) throws Exception {
        MetaBufferEstimator estimator = new MetaBufferEstimator(sampleFeature);
        for (int i = 0; i < ruleCount; i++) {

            final RenderedImage image = ImageUtils.createImage(w, h, null, transparent);
            final Map<RenderingHints.Key, Object> hintsMap = new HashMap<>();
            final Graphics2D graphics =
                    ImageUtils.prepareTransparency(
                            transparent, LegendUtils.getBackgroundColor(legendOptions), image, hintsMap);
            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Feature sample = getSampleFeatureForRule(layer, sampleFeature, applicableRules[i]);

            final List<Symbolizer> symbolizers = applicableRules[i].symbolizers();
            final GraphicLegend graphic = applicableRules[i].getLegend();

            // If this rule has a legend graphic defined in the SLD, use it
            if (graphic != null) {
                if (this.samplePoint == null) {
                    Coordinate coord = new Coordinate(w / 2.0, h / 2.0);

                    try {
                        this.samplePoint =
                                new LiteShape2(geomFac.createPoint(coord), null, null, false);
                    } catch (Exception e) {
                        this.samplePoint = null;
                    }
                }
                shapePainter.paint(graphics, this.samplePoint, graphic, -1.0, false);

            } else {
                for (Symbolizer symbolizer : symbolizers) {
                    // skip raster symbolizers
                    if (!(symbolizer instanceof RasterSymbolizer)) {
                        // rescale symbols if needed
                        LiteShape2 shape = getSampleShape(symbolizer, w, h, w, h);
                        if (rescalingRequired
                                && (symbolizer instanceof PointSymbolizer
                                || symbolizer instanceof LineSymbolizer)) {
                            double size =
                                    getSymbolizerSize(estimator, symbolizer, Math.min(w, h) - 4);
                            double newSize = rescaler.apply(size);
                            symbolizer = rescaleSymbolizer(symbolizer, size, newSize);
                        } else if (symbolizer instanceof PolygonSymbolizer) {
                            // need to make room for the stroke in the symbol, thus, a
                            // smaller rect
                            double symbolizerSize = getSymbolizerSize(estimator, symbolizer, 0);
                            int rescaledWidth = integerSize(minimumSymbolSize, w - symbolizerSize);
                            int rescaledHeight = integerSize(minimumSymbolSize, h - symbolizerSize);
                            shape = getSampleShape(symbolizer, rescaledWidth, rescaledHeight, w, h);

                            symbolizer = rescaleSymbolizer(symbolizer, w, rescaledWidth);
                        }

                        Style2D style2d = styleFactory.createStyle(sample, symbolizer, scaleRange);
                        if (style2d != null) {
                            shapePainter.paint(graphics, shape, style2d, -1.0);
                        }
                    }
                }
            }
            if (titleImage != null) {
                layersImages.add(titleImage);
                titleImage = null;
            }
            legendsStack.add(image);
            graphics.dispose();
        }
        int labelMargin = 3;
        if (!StringUtils.isEmpty((CharSequence) legendOptions.get("labelMargin"))) {
            labelMargin =
                    Integer.parseInt(legendOptions.get("labelMargin").toString());
        }
        LegendMerger.MergeOptions options =
                LegendMerger.MergeOptions.createFromRequest(
                        legendsStack,
                        0,
                        0,
                        0,
                        labelMargin,
                        legendOptions,
                        forceLabelsOn,
                        forceLabelsOff);
        if (ruleCount > 0) {
            BufferedImage image = LegendMerger.mergeLegends(applicableRules, legendOptions, options, (int) legendOptions.get("width"));

            if (image != null) {
                layersImages.add(image);
            }
        }
    }

    private int integerSize(double minimumSymbolSize, double size) {
        return (int) Math.ceil(Math.max(minimumSymbolSize, size));
    }

    @Override
    public Symbolizer rescaleSymbolizer(Symbolizer symbolizer, double size, double newSize) {
        // perform a unit-less rescale
        double scaleFactor = newSize / size;
        RescaleStyleVisitor rescaleVisitor =
                new RescaleStyleVisitor(scaleFactor) {
                    @Override
                    protected Expression rescale(Expression expr) {
                        if (expr == null) {
                            return null;
                        } else if (expr instanceof Literal) {
                            Double value = expr.evaluate(null, Double.class);
                            return ff.literal(value * scaleFactor);
                        } else {
                            return ff.multiply(expr, ff.literal(scaleFactor));
                        }
                    }
                };
        symbolizer.accept(rescaleVisitor);
        symbolizer = (Symbolizer) rescaleVisitor.getCopy();
        return symbolizer;
    }

    /**
     * Receives a list of <code>BufferedImages</code> and produces a new one which holds all the
     * images in <code>imageStack</code> one above the other, handling labels.
     *
     * @param imageStack the list of BufferedImages, one for each applicable Rule
     * @param legendOptions The request.
     * @param forceLabelsOn true for force labels on also with a single image.
     * @param forceLabelsOff true for force labels off also with more than one rule.
     * @return the stack image with all the images on the argument list.
     * @throws IllegalArgumentException if the list is empty
     */
    private BufferedImage mergeGroups(
            List<RenderedImage> imageStack,
            Map<String, Object> legendOptions,
            boolean forceLabelsOn,
            boolean forceLabelsOff) throws Exception {
        LegendMerger.MergeOptions options =
                LegendMerger.MergeOptions.createFromRequest(
                        imageStack, 0, 0, 0, 0, legendOptions, forceLabelsOn, forceLabelsOff);
        return LegendMerger.mergeGroups(null, options);
    }

}

