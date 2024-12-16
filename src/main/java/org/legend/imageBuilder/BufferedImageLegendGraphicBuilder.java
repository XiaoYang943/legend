/*
 * Legend is a library that generates a legend.
 * Legend is developed by CNRS http://www.cnrs.fr/.
 *
 * Most of the code had been picked up from Geoserver (https://github.com/geoserver/geoserver). Legend is free software;
 * you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * Legend is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details http://www.gnu.org/licenses.
 *
 *
 *For more information, please consult: http://www.orbisgis.org
 *or contact directly: info_at_orbisgis.org
 *
 */

package org.legend.imageBuilder;

import org.geotools.api.feature.Feature;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.filter.expression.Literal;
import org.geotools.api.style.*;
import org.geotools.geometry.jts.LiteShape2;
import org.geotools.map.FeatureLayer;
import org.geotools.renderer.lite.MetaBufferEstimator;
import org.geotools.renderer.lite.StyledShapePainter;
import org.geotools.renderer.style.SLDStyleFactory;
import org.geotools.renderer.style.Style2D;
import org.geotools.styling.visitor.RescaleStyleVisitor;
import org.geotools.util.NumberRange;
import org.legend.options.LegendOptions;
import org.legend.utils.ImageUtils;
import org.legend.utils.LegendMerger;
import org.legend.utils.LegendUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Template based on <a
 * href="<a href="http://svn.geotools.org/geotools/trunk/gt/module/main/src/org/geotools/renderer/lite/StyledShapePainter.java">http://svn.geotools.org/geotools/trunk/gt/module/main/src/org/geotools/renderer/lite/StyledShapePainter.java</a>">
 * GeoTools StyledShapePainter</a> that produces a BufferedImage with the appropriate legend graphic
 * for a given layerList and legendOptions.
 *
 * <p>This class takes literally the fact that the arguments <code>WIDTH</code> and <code>HEIGHT
 * </code> are just <i>hints</i> about the desired dimensions of the produced graphic, and the need
 * to produce a legend graphic representative enough of the SLD style for which it is being
 * generated. Thus, if no <code>RULE</code> parameter was passed and the style has more than one
 * applicable Rule for the actual scale factor, there will be generated a legend graphic of the
 * specified width, but with as many stacked graphics as applicable rules were found, providing by
 * this way a representative enough legend.
 * <p>
 * Comes from the package org.geoserver.wms.legendgraphic
 *
 * @author Adrien Bessy (entirely based on what Gabriel Roldan and Simone Giannecchini did with GeoServer)
 */
public class BufferedImageLegendGraphicBuilder extends LegendGraphicBuilder {

    /**
     * Singleton shape painter to serve all legend requests. We can use a single shape painter
     * instance as long as it remains thread safe.
     */
    private static final StyledShapePainter shapePainter = new StyledShapePainter();

    /**
     * Default constructor. Subclasses may provide its own with a String parameter to establish its
     * desired output format, if they support more than one (e.g. a JAI based one)
     */
    public BufferedImageLegendGraphicBuilder() {
        super();
    }

    /**
     * Takes a featureLayerList and legendOptions and produces a BufferedImage.
     *
     * @param featureLayerList feature layer list
     * @param legendOptions    legend options map that can contain information like icon width, icon height, forceRuleLabelsOff, ...)
     * @return the buffered image
     * @throws Exception if there are problems creating a "sample" feature instance for the FeatureType returns as the required layer (which should not occur).
     */
    public BufferedImage buildLegendGraphic(List<FeatureLayer> featureLayerList, LegendOptions legendOptions) throws Exception {
        setup(legendOptions);
        // list of images to be rendered for the layers (more than one if a layer list is given)
        List<RenderedImage> layersImages = new ArrayList<>();

        for (FeatureLayer featureLayer : featureLayerList) {
            // style and rule to use for the current layer
            Style gt2Style = featureLayer.getStyle();
            if (gt2Style == null) {
                throw new NullPointerException("There is no style in featureLayer");
            }
            final FeatureTypeStyle[] ftStyles = gt2Style.featureTypeStyles().toArray(new FeatureTypeStyle[0]);
            Rule[] rules = LegendUtils.getRules1(ftStyles);

            RenderedImage titleImage = null;
            // we put a title on top of each style legend
            if (!forceTitlesOff) {
                titleImage = getLayerTitle(featureLayer, width, height, isTransparent, legendOptions);
            }

            /*
             * Default minimum size for symbols rendering.
             */
            double minimumSymbolSize = 3.0;

            // calculate the symbols rescaling factor necessary for them to be
            // drawn inside the icon box
            int defaultSize = Math.min(width, height);

            FeatureType featureType = featureLayer.getFeatureSource().getSchema();
            Feature sampleFeature = createSampleFeature(featureType);
            double[] minMax = calcSymbolSize(defaultSize, minimumSymbolSize, sampleFeature, rules);
            boolean rescalingRequired = false;
            java.util.function.Function<Double, Double> rescaler = size -> (size / minMax[1]) * defaultSize;

            final SLDStyleFactory styleFactory = new SLDStyleFactory();
            final double scaleDenominator = -1.0;
            final NumberRange<Double> scaleRange = NumberRange.create(scaleDenominator, scaleDenominator);
            final int ruleCount = rules.length;
            final List<RenderedImage> legendsStack = new ArrayList<>(ruleCount);
            renderRules(
                    layersImages,
                    forceLabelsOn,
                    forceLabelsOff,
                    isTransparent,
                    titleImage,
                    sampleFeature,
                    rules,
                    scaleRange,
                    ruleCount,
                    legendsStack,
                    styleFactory,
                    minimumSymbolSize,
                    rescalingRequired,
                    rescaler, legendOptions);
        }
        // all legend graphics are merged if we have a layer group
        BufferedImage finalLegend = mergeGroups(layersImages, forceLabelsOn, forceLabelsOff, legendOptions);
        if (finalLegend == null) {
            throw new IllegalArgumentException("no legend passed");
        }

        // create marge
        BufferedImage BufferedImageForMarge = new BufferedImage(finalLegend.getWidth() + marge * 2, finalLegend.getHeight() + marge * 2, BufferedImage.TYPE_INT_RGB);
        Graphics g = BufferedImageForMarge.getGraphics();
        g.setColor(LegendUtils.getBackgroundColor(legendOptions));
        g.fillRect(0, 0, finalLegend.getWidth() + marge * 2, finalLegend.getHeight() + marge * 2);
        g.drawImage(finalLegend, marge, marge, null);

        return BufferedImageForMarge;
    }

    /**
     * Renders a title for a layer (to be put on top of the layer legend).
     *
     * @param featureLayer  FeatureType representing the layer
     * @param w             width for the image (hint)
     * @param h             height for the image (hint)
     * @param transparent   (should the image be transparent)
     * @param legendOptions legendOptions like horizontalMarginBetweenLayers, ruleLabelMargin, ...
     * @return the title image
     */
    private RenderedImage getLayerTitle(FeatureLayer featureLayer, int w, int h, boolean transparent,
                                        LegendOptions legendOptions) {
        String title;
        // checks layer title, otherwise style title
        String inputTitle = legendOptions.getTitle();
        if (inputTitle != null && !inputTitle.isEmpty()) {
            title = inputTitle;
        } else if (featureLayer.getTitle() != null) {
            title = featureLayer.getTitle();
        } else {
            title = "图例";
        }
        final BufferedImage image = ImageUtils.createImage(w, h, null, transparent);
        return LegendMerger.getRenderedLabel(image, title, legendOptions);
    }

    /**
     * Create images and add them in the layersImages.
     *
     * @param legendOptions     legendOptions like horizontalMarginBetweenLayers, ruleLabelMargin, ...
     * @param layersImages      layersImages
     * @param forceLabelsOn     forceLabelsOn
     * @param forceLabelsOff    forceLabelsOff
     * @param transparent       transparent
     * @param titleImage        titleImage
     * @param sampleFeature     sampleFeature
     * @param applicableRules   applicableRules
     * @param scaleRange        scaleRange
     * @param ruleCount         ruleCount
     * @param legendsStack      legendsStack
     * @param styleFactory      styleFactory
     * @param minimumSymbolSize minimumSymbolSize
     * @param rescalingRequired rescalingRequired
     * @param rescaler          rescaler
     */
    private void renderRules(
            List<RenderedImage> layersImages,
            boolean forceLabelsOn,
            boolean forceLabelsOff,
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
            Function<Double, Double> rescaler, LegendOptions legendOptions) throws Exception {
        MetaBufferEstimator estimator = new MetaBufferEstimator(sampleFeature);
        for (int i = 0; i < ruleCount; i++) {
            final RenderedImage image = ImageUtils.createImage(width, height, null, transparent);
            final Map<RenderingHints.Key, Object> hintsMap = new HashMap<>();
            final Graphics2D graphics = ImageUtils.prepareTransparency(transparent, LegendUtils.getBackgroundColor(legendOptions), image, hintsMap);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            final List<Symbolizer> symbolizers = applicableRules[i].symbolizers();

            for (Symbolizer symbolizer : symbolizers) {
                // skip raster symbolizers
                if (!(symbolizer instanceof RasterSymbolizer)) {
                    // rescale symbols if needed
                    LiteShape2 shape = getSampleShape(symbolizer, width, height, width, height);
                    if (rescalingRequired && (symbolizer instanceof PointSymbolizer || symbolizer instanceof LineSymbolizer)) {
                        double size = getSymbolizerSize(estimator, symbolizer, Math.min(width, height) - 4);
                        double newSize = rescaler.apply(size);
                        symbolizer = rescaleSymbolizer(symbolizer, size, newSize);
                    } else if (symbolizer instanceof PolygonSymbolizer) {
                        // need to make room for the stroke in the symbol, thus, a smaller rectangle
                        double symbolizerSize = getSymbolizerSize(estimator, symbolizer, 0);
                        int rescaledWidth = rescaleSize(minimumSymbolSize, width - symbolizerSize);
                        int rescaledHeight = rescaleSize(minimumSymbolSize, height - symbolizerSize);
                        shape = getSampleShape(symbolizer, rescaledWidth, rescaledHeight, width, height);
                        symbolizer = rescaleSymbolizer(symbolizer, width, rescaledWidth);
                    }

                    Style2D style2d = styleFactory.createStyle(sampleFeature, symbolizer, scaleRange);
                    if (style2d != null) {
                        shapePainter.paint(graphics, shape, style2d, -1.0);
                    }
                    if (titleImage != null) {
                        layersImages.add(titleImage);
                        titleImage = null;
                    }
                    legendsStack.add(image);
                    graphics.dispose();
                }
            }
        }

        LegendMerger.MergeOptions options = LegendMerger.MergeOptions.createFromOptions(legendsStack, 0, forceLabelsOn,
                forceLabelsOff, legendOptions);
        if (ruleCount > 0) {
            BufferedImage image = LegendMerger.mergeLegends(applicableRules, options, legendOptions);
            if (image != null) {
                layersImages.add(image);
            }
        }
    }

    /**
     * Rescales the size
     *
     * @param minimumSymbolSize the minimum symbolizer size
     * @param size              the size
     * @return the rescaled size.
     */
    private int rescaleSize(double minimumSymbolSize, double size) {
        return (int) Math.ceil(Math.max(minimumSymbolSize, size));
    }

    /**
     * Rescales the size of a symbolizer
     *
     * @param symbolizer the symbolizer
     * @param size       the size
     * @param newSize    the newSize
     * @return the rescaled symbolizer.
     */
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
     * @param imageStack     the list of BufferedImages, one for each applicable Rule
     * @param legendOptions  The legend options.
     * @param forceLabelsOn  true for force labels on also with a single image.
     * @param forceLabelsOff true for force labels off also with more than one rule.
     * @return the stack image with all the images on the argument list.
     * @throws IllegalArgumentException if the list is empty
     */
    private BufferedImage mergeGroups(List<RenderedImage> imageStack,
                                      boolean forceLabelsOn, boolean forceLabelsOff, LegendOptions legendOptions) throws Exception {
        LegendMerger.MergeOptions options = LegendMerger.MergeOptions.createFromOptions(imageStack, 0,
                forceLabelsOn, forceLabelsOff, legendOptions);
        return LegendMerger.mergeGroups(null, options, legendOptions);
    }

}

