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
    public BufferedImage paintMapScale(String bars){
        double pixelScaleFor500m = 500 * imageWidth / worldWidth;
        double pixelScaleFor1000m = 1000 * imageWidth / worldWidth;
        BufferedImage scaleBufferedImage = new BufferedImage(425, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D graph2d = scaleBufferedImage.createGraphics();
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0, 0, 425, 400);
        graph2d.setPaint( Color.BLACK );

        double marge = (scaleBufferedImage.getWidth()-pixelScaleFor500m)/2;

        if(bars.equalsIgnoreCase("downBars")) {
            // horizontal line
            graph2d.draw( new Line2D.Double( marge, 0, marge + pixelScaleFor1000m + pixelScaleFor500m, 0 ) );
            // vertical lines
            graph2d.draw(new Line2D.Double(marge, 0, marge, 5));
            graph2d.draw(new Line2D.Double(marge + pixelScaleFor500m, 0, marge + pixelScaleFor500m, 5));
            graph2d.draw(new Line2D.Double(marge + pixelScaleFor1000m, 0, marge + pixelScaleFor1000m, 5));
            graph2d.draw(new Line2D.Double(marge + pixelScaleFor1000m + pixelScaleFor500m, 0, marge + pixelScaleFor1000m + pixelScaleFor500m, 5));
            graph2d.drawString("0", (int) marge -3, 20);
            graph2d.drawString("500", (int) marge + (int) pixelScaleFor500m - 10, 20);
            graph2d.drawString("1000", (int) marge + (int) pixelScaleFor1000m - 17, 20);
            graph2d.drawString("1500", (int) marge + (int) pixelScaleFor1000m + (int) pixelScaleFor500m  - 17, 20);
        } else if(bars.equalsIgnoreCase("upBars")){
            // horizontal line
            graph2d.draw( new Line2D.Double( marge, 50, marge + pixelScaleFor1000m + pixelScaleFor500m, 50 ) );
            // vertical lines
            graph2d.draw(new Line2D.Double(marge, 50, marge, 45));
            graph2d.draw(new Line2D.Double(marge + pixelScaleFor500m, 50, marge + pixelScaleFor500m, 45));
            graph2d.draw(new Line2D.Double(marge + pixelScaleFor1000m, 50, marge + pixelScaleFor1000m, 45));
            graph2d.draw(new Line2D.Double(marge + pixelScaleFor1000m + pixelScaleFor500m, 50, marge + pixelScaleFor1000m + pixelScaleFor500m, 45));
            graph2d.drawString("0", (int) marge - 3, 30);
            graph2d.drawString("500", (int) marge + (int) pixelScaleFor500m - 10, 30);
            graph2d.drawString("1000", (int) marge + (int) pixelScaleFor1000m - 17, 30);
            graph2d.drawString("1500", (int) marge + (int) pixelScaleFor1000m + (int) pixelScaleFor500m  - 17, 30);
        } else if (bars.equalsIgnoreCase("thickHorizontalBar")) {
            graph2d.drawRect((int) marge, 50, (int) pixelScaleFor500m, 5);
            graph2d.fillRect((int) marge, 50, (int) pixelScaleFor500m, 5);
            graph2d.drawRect((int) marge + (int) pixelScaleFor500m, 50, (int) pixelScaleFor500m, 5);
            graph2d.drawRect((int) marge + (int) pixelScaleFor500m + (int) pixelScaleFor500m, 50, (int) pixelScaleFor500m, 5);
            graph2d.fillRect((int) marge + (int) pixelScaleFor500m + (int) pixelScaleFor500m, 50, (int) pixelScaleFor500m, 5);
            graph2d.drawString("0", (int) marge-3, 40);
            graph2d.drawString("500", (int) marge + (int) pixelScaleFor500m - 12, 40);
            graph2d.drawString("1000", (int) marge + (int) pixelScaleFor1000m - 17, 40);
            graph2d.drawString("1500", (int) marge + (int) pixelScaleFor1000m + (int) pixelScaleFor500m  - 17, 40);
        }
        return scaleBufferedImage;
    }

    public void setPosition(String position, BufferedImage image, BufferedImage scaleBufferedImage) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        switch (position){
            case "bottom":
                positionY = imgHeight - scaleBufferedImage.getHeight() / 3;
                positionX = imgWidth/2 - scaleBufferedImage.getWidth() * 2 / 3;
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
            case "topLeft":
                positionY = imgHeight/20;
                positionX = imgWidth/25;
                break;
            case "topRight":
                positionY = imgHeight/20;
                positionX = imgWidth - imgWidth/5;
                break;
            default:
                positionX = Integer.parseInt(position.split(":")[0]);
                positionY = Integer.parseInt(position.split(":")[1]);
        }
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
}
