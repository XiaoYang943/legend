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

package org.legend.model;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.FileUtils;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Provides methods to build a compass buffered image.
 *
 * @author Adrien Bessy
 */
public class Compass extends Item{

    static CoordinateReferenceSystem GROUND;
    static {
        try {
            GROUND = CRS.decode("EPSG:4326"); //$NON-NLS-1$
        } catch (FactoryException e) {
            GROUND = DefaultGeographicCRS.WGS84;
        }
    }

    String filePath;

    public Compass(String filePath){
        this.filePath = filePath;
    }

    /**
     * Create a bufferedImage, paint a compass, then return the bufferedImage.
     * @return the buffered image
     */
    public BufferedImage paintCompass(int size) throws IOException {
        return rasterize(new File(filePath), size);
    }

    /**
     * Transform svg file to png file
     * @param svgFile the svg file
     * @return the buffered image
     */
    public static BufferedImage rasterize(File svgFile, int size) throws IOException {
        final BufferedImage[] imagePointer = new BufferedImage[1];
        // Rendering hints can't be set programatically, so
        // we override defaults with a temporary stylesheet.
        // These defaults emphasize quality and precision, and
        // are more similar to the defaults of other SVG viewers.
        // SVG documents can still override these defaults.
        String css = "svg {" +
                "shape-rendering: geometricPrecision;" +
                "text-rendering:  geometricPrecision;" +
                "color-rendering: optimizeQuality;" +
                "image-rendering: optimizeQuality;" +
                "}";
        File cssFile = File.createTempFile("batik-default-override-", ".css");
        FileUtils.writeStringToFile(cssFile, css);

        TranscodingHints transcoderHints = new TranscodingHints();
        transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
        transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION,
                SVGDOMImplementation.getDOMImplementation());
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
                SVGConstants.SVG_NAMESPACE_URI);
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
        transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString());
        transcoderHints.put(ImageTranscoder.KEY_WIDTH, (float) 150 + size);
        transcoderHints.put(ImageTranscoder.KEY_HEIGHT, (float) 150 + size);

        try {
            TranscoderInput input = new TranscoderInput(Files.newInputStream(svgFile.toPath()));
            ImageTranscoder t = new ImageTranscoder() {
                @Override
                public BufferedImage createImage(int w, int h) {
                    return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                }

                @Override
                public void writeImage(BufferedImage image, TranscoderOutput out) {
                    imagePointer[0] = image;
                }
            };
            t.setTranscodingHints(transcoderHints);
            t.transcode(input, null);
        }
        catch (TranscoderException ex) {
            // Requires Java 6
            ex.printStackTrace();
            throw new IOException("Couldn't convert " + svgFile);
        }
        return imagePointer[0];
    }

    public void setNorth(MapContent mapContent, int width, int height){
        Coordinate worldStart = pixelToWorld(width, height, mapContent.getMaxBounds(), mapContent.getViewport().getScreenArea().getSize());
        Coordinate groundStart = toGround( mapContent, worldStart );
        Coordinate groundNorth = moveNorth( groundStart );
        Coordinate worldNorth = fromGround( mapContent, groundNorth );
        double theta = theta( worldStart, worldNorth );
        System.out.println("theta : " + theta);
    }


    public static AffineTransform worldToScreenTransform(Envelope mapExtent,
                                                         Dimension screenSize) {
        double scaleX = screenSize.getWidth() / mapExtent.getWidth();
        double scaleY = screenSize.getHeight() / mapExtent.getHeight();

        double tx = -mapExtent.getMinX() * scaleX;
        double ty = (mapExtent.getMinY() * scaleY) + screenSize.getHeight();

        AffineTransform at = new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY,
                tx, ty);

        return at;
    }

    public static Coordinate pixelToWorld(double x, double y,
                                          ReferencedEnvelope extent, Dimension displaySize) {
        // set up the affine transform and calculate scale values
        AffineTransform at = worldToScreenTransform(extent, displaySize);

        try {
            Point2D result = at.inverseTransform(
                    new java.awt.geom.Point2D.Double(x, y),
                    new java.awt.geom.Point2D.Double());
            Coordinate c = new Coordinate(result.getX(), result.getY());

            return c;
        } catch (Exception e) {
        }

        return null;
    }

    /** Will transform there into ground WGS84 coordinates or die (ie null) trying */
    private Coordinate toGround(MapContent context, Coordinate there) {

        if( GROUND.equals( context.getViewport().getCoordinateReferenceSystem()) ){
            return there;
        }
        try {
            MathTransform transform = CRS.findMathTransform( context.getViewport().getCoordinateReferenceSystem(), GROUND );
            return JTS.transform( there, null, transform );
        } catch (FactoryException e) {
            e.printStackTrace();
            return null;
        } catch (TransformException e) {
            // yes I do
            return null;
        }
    }

    /** A coordinate that is slightly north please */
    private Coordinate moveNorth(Coordinate ground) {
        double up = ground.y+0.1;
        if( up > 90.0 ){
            return new Coordinate( ground.x, 90.0 );
        }
        return new Coordinate( ground.x, up);
    }

    private Coordinate fromGround(MapContent context, Coordinate ground) {

        if( GROUND.equals( context.getViewport().getCoordinateReferenceSystem()) ){
            return ground;
        }
        try {
            MathTransform transform = CRS.findMathTransform( GROUND, context.getViewport().getCoordinateReferenceSystem() );
            return JTS.transform( ground, null, transform );
        } catch (FactoryException e) {
            // I hate you
            return null;
        } catch (TransformException e) {
            // yes I do
            return null;
        }
    }

    private double theta(Coordinate ground, Coordinate north) {
        return Math.atan2(Math.abs(north.y - ground.y), Math.abs(north.x - ground.x));
    }

}
