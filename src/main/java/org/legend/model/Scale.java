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

import org.geotools.map.MapContent;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * This class allows to create a map scale to add to your map.
 *
 * @author Adrien Bessy
 */
public class Scale {

    double worldWidth;
    int imageWidth;
    int positionX;
    int positionY;

    public Scale(MapContent mapContent, int imageWidth){
        this.worldWidth = mapContent.getViewport().getBounds().getWidth();
        this.imageWidth = imageWidth;
    }

    /**
     * Create a bufferedImage, paint a map scale for 100 and 500 metres, then return the bufferedImage.
     * @return the buffered image
     */
    public BufferedImage paintMapScale(){
        double pixelScaleFor100m = 100 * imageWidth / worldWidth;
        double pixelScaleFor500m = 500 * imageWidth / worldWidth;
        BufferedImage scaleBufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graph2d = scaleBufferedImage.createGraphics();
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0, 0, 200, 200);
        graph2d.setPaint( Color.BLACK );

        double marge = (scaleBufferedImage.getWidth()-pixelScaleFor500m)/2;
        // horizontal line
        graph2d.draw( new Line2D.Double( marge, 0, marge + pixelScaleFor500m, 0 ) );
        // vertical lines
        graph2d.draw( new Line2D.Double( marge + pixelScaleFor100m, 0, marge + pixelScaleFor100m, 5 ) );
        graph2d.draw( new Line2D.Double( marge, 0, marge, 5 ) );
        graph2d.draw( new Line2D.Double( marge + pixelScaleFor500m, 0, marge + pixelScaleFor500m, 5 ) );
        graph2d.drawString("100", (int) marge + (int) pixelScaleFor100m - 10, 20);
        graph2d.drawString("500 m", (int) marge + (int) pixelScaleFor500m - 10, 20);
        return scaleBufferedImage;
    }

    public void setPosition(String position, BufferedImage image, BufferedImage scaleBufferedImage) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getWidth();
        switch (position){
            case "bottom":
                positionY = imgHeight - scaleBufferedImage.getHeight()/2;
                positionX = imgWidth/2 - scaleBufferedImage.getWidth()/2;
                break;
            case "bottomLeft":
                positionY = imgHeight - imgHeight/5;
                positionX = imgWidth/5 - scaleBufferedImage.getWidth()/2;
                break;
            case "bottomRight":
                positionY = imgHeight - imgHeight/5;
                positionX = imgWidth - imgWidth/5 - scaleBufferedImage.getWidth()/2;
                break;
            case "top":
                positionY = scaleBufferedImage.getHeight()/2;
                positionX = imgWidth/2 - scaleBufferedImage.getWidth()/2;
                break;
        }
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
}
