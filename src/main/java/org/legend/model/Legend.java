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

import java.awt.image.BufferedImage;

/**
 * Provides methods to build a legend buffered image.
 *
 * @author Adrien Bessy
 */
public class Legend {

    static int positionX;
    static int positionY;

    /**
     * Set position of the legend on the base frame
     * @param position the position (can be absolute position like "[40:10]")
     * @param imgWidth the width of the base frame
     * @param imgHeight the height of the base frame
     * @param legendBufferedImage the buffered image of the legend
     */
    public void setPosition(String position, int imgWidth, int imgHeight, BufferedImage legendBufferedImage) {
        switch (position){
            case "bottom":
                positionY = imgHeight - legendBufferedImage.getHeight()/2;
                positionX = imgWidth/2 - legendBufferedImage.getWidth()/2;
                break;
            case "bottomLeft":
                positionY = imgHeight - legendBufferedImage.getHeight() - imgHeight/15;
                positionX = legendBufferedImage.getWidth()/3;
                break;
            case "bottomRight":
                positionY = imgHeight - legendBufferedImage.getHeight() - imgHeight/15;
                positionX = imgWidth - legendBufferedImage.getWidth() - imgWidth/60;
                break;
            case "top":
                positionY = legendBufferedImage.getHeight()/2;
                positionX = imgWidth/2 - legendBufferedImage.getWidth()/2;
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
