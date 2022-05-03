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

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Provides methods to build a base frame buffered image where a map and decorations can then be painted.
 *
 * @author Adrien Bessy
 */
public class BaseFrame {

    BufferedImage mapImage;
    int imgWidth;
    int imgHeight;
    private int margin;
    private BufferedImage baseFrameBufferedImage;

    public void setBaseFrameSize(BufferedImage mapImage,int margin) {
        this.mapImage = mapImage;
        this.imgWidth = mapImage.getWidth() + margin;
        this.imgHeight = mapImage.getHeight() + margin;
        this.margin = margin;
    }

    public void setBufferedImage(){
        this.baseFrameBufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Paint map on the base frame, set its position and returns the graphics2d
     * @param position the position (can be absolute position like "[40:10]")
     * @return the graphics2D
     */
    public Graphics2D paintMapOnBaseFrame(String position) {
        Graphics2D g = (Graphics2D) baseFrameBufferedImage.getGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0,0, imgWidth, imgHeight);
        if(margin>0) {
            switch (position) {
                case "bottom":
                    g.drawImage(mapImage, margin / 2, margin, null);
                    break;
                case "right":
                    g.drawImage(mapImage, margin, margin / 2, null);
                    break;
                case "top":
                    g.drawImage(mapImage, margin / 2, 0, null);
                    break;
                case "left":
                    g.drawImage(mapImage, 0, margin / 2, null);
                    break;
                case "center":
                    g.drawImage(mapImage, margin / 2, margin / 2, null);
                    break;
                default:
                    g.drawImage(mapImage, Integer.parseInt(position.split(":")[0]), Integer.parseInt(position.split(":")[1]), null);
            }
        }else{
            g.drawImage(mapImage, 0, 0, null);
        }
        return g;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public BufferedImage getBaseFrameBufferedImage() {
        return baseFrameBufferedImage;
    }
}
