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

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Provides methods to build a compass buffered image.
 *
 * @author Adrien Bessy
 */
public class Compass extends ImageComponent {

    static CoordinateReferenceSystem GROUND;
    static {
        try {
            GROUND = CRS.decode("EPSG:4326"); //$NON-NLS-1$
        } catch (FactoryException e) {
            GROUND = DefaultGeographicCRS.WGS84;
        }
    }

    public Compass(String filePath){
        this.filePath = filePath;
    }

    /**
     * Get the angle allowing to the raw or compass to point the north
     * @param mapContent the map content
     * @param width the width of the map document
     * @param height the height of the map document
     * @return the angle
     */
    public double getRotationToNorth(MapContent mapContent, int width, int height){
        Coordinate worldStart = pixelToWorld(width, height, mapContent.getMaxBounds(), mapContent.getViewport().getScreenArea().getSize());
        Coordinate groundStart = toGround( mapContent, worldStart );
        assert groundStart != null;
        Coordinate groundNorth = moveNorth( groundStart );
        Coordinate worldNorth = fromGround( mapContent, groundNorth );
        assert worldStart != null;
        assert worldNorth != null;
        return theta( worldStart, worldNorth );
    }

    /**
     * Get the angle between both coordinates
     * @param north the north coordinates
     * @param ground the coordinates
     * @return the angle
     * */
    private double theta(Coordinate ground, Coordinate north) {
        return Math.atan2(Math.abs(north.y - ground.y), Math.abs(north.x - ground.x));
    }

    /**
     * Get the affineTransform
     * @param mapExtent the mapExtent
     * @param screenSize the screen size
     * @return the affine transform
     */
    public static AffineTransform worldToScreenTransform(Envelope mapExtent, Dimension screenSize) {
        double scaleX = screenSize.getWidth() / mapExtent.getWidth();
        double scaleY = screenSize.getHeight() / mapExtent.getHeight();

        double tx = -mapExtent.getMinX() * scaleX;
        double ty = (mapExtent.getMinY() * scaleY) + screenSize.getHeight();

        return new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY,
                tx, ty);
    }

    /**
     * Get the coordinates
     * @param x the x
     * @param y the y
     * @param extent the envelope
     * @param displaySize the display size
     * @return the coordinates
     */
    public static Coordinate pixelToWorld(double x, double y, ReferencedEnvelope extent, Dimension displaySize) {
        // set up the affine transform and calculate scale values
        AffineTransform at = worldToScreenTransform(extent, displaySize);
        try {
            Point2D result = at.inverseTransform(
                    new java.awt.geom.Point2D.Double(x, y),
                    new java.awt.geom.Point2D.Double());

            return new Coordinate(result.getX(), result.getY());
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Will transform there into ground WGS84 coordinates or die (ie null) trying
     * @param context the map content
     * @param there the coordinates
     * @return the coordinates
     * */
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

    /**
     * Get a coordinate that is slightly north
     * @param ground the coordinates
     * @return the coordinates
     * */
    private Coordinate moveNorth(Coordinate ground) {
        double up = ground.y+0.1;
        if( up > 90.0 ){
            return new Coordinate( ground.x, 90.0 );
        }
        return new Coordinate( ground.x, up);
    }

    /**
     * Get the coordinates from ground
     * @param context the map content
     * @param ground the coordinates
     * @return the coordinates
     * */
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

}
