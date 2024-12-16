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
import org.geotools.api.feature.IllegalAttributeException;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.filter.expression.Literal;
import org.geotools.api.style.*;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.LiteShape2;
import org.geotools.renderer.lite.MetaBufferEstimator;
import org.geotools.styling.visitor.RescaleStyleVisitor;
import org.legend.options.LegendOptions;
import org.legend.utils.LegendUtils;
import org.locationtech.jts.geom.*;

/**
 * Comes from the package org.geoserver.wms.legendgraphic
 *
 * @author Adrien Bessy, (entirely based on what ian did with GeoServer)
 */
public abstract class LegendGraphicBuilder {

    /**
     * used to create sample point shapes with LiteShape (not lines nor polygons)
     */
    protected static final GeometryFactory geomFac = new GeometryFactory();
    /**
     * Just a holder to avoid creating many line shapes from inside <code>getSampleShape()</code>
     */
    private LiteShape2 sampleLine;
    /**
     * Just a holder to avoid creating many point shapes from inside <code>getSampleShape()</code>
     */
    protected LiteShape2 samplePoint;

    int width = 50;
    int height = 50;
    boolean forceLabelsOn = true; //rule label
    boolean forceLabelsOff = false;
    boolean forceTitlesOff = false; // layer title otherwise style title
    boolean isTransparent = false;
    int marge = 10;

    /**
     *
     */
    public LegendGraphicBuilder() {
        super();
    }

    public void setup(LegendOptions legendOptionsNew) {
        width = legendOptionsNew.getWidth();
        height = legendOptionsNew.getHeight();
        isTransparent = legendOptionsNew.isTransparent();
    }

    /**
     * Returns a <code>java.awt.Shape</code> appropriate to render a legend graphic given the
     * symbolizer type and the legend dimensions.
     *
     * @param symbolizer           the Symbolizer for whose type a sample shape will be created
     * @param rescaledLegendWidth  the rescaled width, in output units, of the legend graphic for PolygonSymbolizer
     * @param rescaledLegendHeight the rescaled height, in output units, of the legend graphic for PolygonSymbolizer
     * @param legendWidth          the requested width, in output units, of the legend graphic
     * @param legendHeight         the requested height, in output units, of the legend graphic
     * @return an appropiate Line2D, Rectangle2D or LiteShape(Point) for the symbolizer, either it
     * is a LineSymbolizer, a PolygonSymbolizer, or a Point ot Text Symbolizer
     */
    protected LiteShape2 getSampleShape(Symbolizer symbolizer, int rescaledLegendWidth, int rescaledLegendHeight,
                                        int legendWidth, int legendHeight) {
        LiteShape2 sampleShape;
        final float hpad = (legendWidth * LegendUtils.hpaddingFactor) + (legendWidth - rescaledLegendWidth) / 2f;
        final float vpad = (legendHeight * LegendUtils.vpaddingFactor) + (legendHeight - rescaledLegendHeight) / 2f;

        if (symbolizer instanceof LineSymbolizer) {
            if (this.sampleLine == null) {
                Coordinate[] coords = {
                        new Coordinate(20, 25),
                        new Coordinate(40, 25)
                };
                LineString geom = geomFac.createLineString(coords);
                try {
                    this.sampleLine = new LiteShape2(geom, null, null, false);
                } catch (Exception e) {
                    this.sampleLine = null;
                }
            }
            sampleShape = this.sampleLine;

        } else if ((symbolizer instanceof PolygonSymbolizer) || (symbolizer instanceof RasterSymbolizer)) {
            final float w = legendWidth - (2 * hpad) - 8;
            final float h = legendHeight - (2 * vpad) - 8;
            Coordinate[] coords = {
                    new Coordinate(hpad, vpad + 2.5),
                    new Coordinate(hpad, vpad + h + 2.5),
                    new Coordinate(hpad + w, vpad + h + 2.5),
                    new Coordinate(hpad + w, vpad + 2.5),
                    new Coordinate(hpad, vpad + 2.5)
            };
            LinearRing shell = geomFac.createLinearRing(coords);
            Polygon geom = geomFac.createPolygon(shell, null);
            try {
                return new LiteShape2(geom, null, null, false);
            } catch (Exception e) {
                return null;
            }

        } else if (symbolizer instanceof PointSymbolizer || symbolizer instanceof TextSymbolizer) {
            if (this.samplePoint == null) {
                Coordinate coord = new Coordinate(rescaledLegendWidth / 2d, rescaledLegendHeight / 2d);
                try {
                    this.samplePoint =
                            new LiteShape2(geomFac.createPoint(coord), null, null, false);
                } catch (Exception e) {
                    this.samplePoint = null;
                }
            }
            sampleShape = this.samplePoint;

        } else {
            throw new IllegalArgumentException("Unknown symbolizer: " + symbolizer);
        }
        return sampleShape;
    }

    /**
     * Returns a rescaled symbolizer
     *
     * @param symbolizer the Symbolizer
     * @param size       the current size
     * @param newSize    the new size
     * @return a rescaled symbolizer
     */
    public Symbolizer rescaleSymbolizer(Symbolizer symbolizer, double size, double newSize) {
        // perform a unit-less rescale
        double scaleFactor = newSize / size;
        RescaleStyleVisitor rescaleVisitor = new RescaleStyleVisitor(scaleFactor) {
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
     * Creates a sample Feature instance in the hope that it can be used in the rendering of the
     * legend graphic.
     *
     * @param featureType the featureType for which to create a sample Feature instance
     * @return a sample feature instance
     */
    protected Feature createSampleFeature(FeatureType featureType) throws Exception {
        Feature sampleFeature;
        try {
            if (featureType instanceof SimpleFeatureType) {
                if (hasMixedGeometry((SimpleFeatureType) featureType)) {
                    // we can't create a sample for a generic Geometry type
                    sampleFeature = null;
                } else {
                    sampleFeature = SimpleFeatureBuilder.template((SimpleFeatureType) featureType, null);
                }
            } else {
                sampleFeature = DataUtilities.templateFeature(featureType);
            }
        } catch (IllegalAttributeException e) {
            throw new Exception(e);
        }
        return sampleFeature;
    }

    /**
     * Checks if the given featureType contains a GeometryDescriptor that has a generic Geometry type.
     *
     * @param featureType the featureType
     * @return true if it contains a GeometryDescriptor that has a generic Geometry type.
     */
    private boolean hasMixedGeometry(SimpleFeatureType featureType) {
        for (AttributeDescriptor attDesc : featureType.getAttributeDescriptors()) {
            if (isMixedGeometry(attDesc)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given AttributeDescriptor describes a generic Geometry.
     *
     * @param attDesc the AttributeDescriptor
     * @return true if it describes a generic Geometry.
     */
    private boolean isMixedGeometry(AttributeDescriptor attDesc) {
        if (attDesc instanceof GeometryDescriptor && attDesc.getType().getBinding() == Geometry.class) {
            return true;
        }
        return false;
    }

    /**
     * Calculates a global rescaling factor for all the symbols to be drawn in the given rules. This
     * is to be sure all symbols are drawn inside the given w x h box.
     *
     * @param defaultMaxSize Math.min(w, h)
     * @param defaultMinSize Default minimum size for symbols rendering.
     * @param feature        Feature to be used for size extraction in expressions (if null a sample
     *                       Feature will be created from featureType)
     * @param rules          set of rules to scan for symbols
     * @return a global rescaling factor.
     */
    protected double[] calcSymbolSize(double defaultMaxSize, double defaultMinSize,
                                      Feature feature, final Rule[] rules) {
        // check for max and min size in rendered symbols
        double minSize = defaultMaxSize;
        double maxSize = defaultMinSize;

        for (Rule rule : rules) {
            MetaBufferEstimator estimator = new MetaBufferEstimator(feature);
            for (Symbolizer symbolizer : rule.symbolizers()) {
                if (symbolizer instanceof PointSymbolizer || symbolizer instanceof LineSymbolizer) {
                    double size = getSymbolizerSize(estimator, symbolizer, defaultMaxSize);
                    // a line symbolizer is depicted as a line of the requested size, so don't go
                    // below min
                    if (size < minSize && !(symbolizer instanceof LineSymbolizer)) {
                        minSize = size;
                    }
                    if (size > maxSize) {
                        maxSize = size;
                    }
                }
            }
        }
        return new double[]{minSize, maxSize};
    }

    /**
     * Gets a numeric value for the given PointSymbolizer
     *
     * @param estimator   estimator
     * @param symbolizer  symbolizer
     * @param defaultSize size to use is none can be taken from the symbolizer
     * @return a numeric value for the given PointSymbolizer.
     */
    protected double getSymbolizerSize(MetaBufferEstimator estimator, Symbolizer symbolizer, double defaultSize) {
        estimator.reset();
        symbolizer.accept(estimator);
        int buffer = estimator.getBuffer();
        if (buffer > 0) {
            return buffer;
        } else {
            return defaultSize;
        }
    }

}
