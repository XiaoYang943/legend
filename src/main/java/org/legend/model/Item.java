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
import java.util.List;

abstract public class Item {

    int positionX;
    int positionY;

    /**
     * Set position of the item on the base frame
     * @param position the position
     * @param baseFrameWidth the width of the base frame
     * @param baseFrameHeight the height of the base frame
     * @param bufferedImage the buffered image of the item
     */
    public void setPosition(String position, int baseFrameWidth, int baseFrameHeight, BufferedImage bufferedImage) {
        switch (position){
            case "bottom":
                positionY = baseFrameHeight - bufferedImage.getHeight() - baseFrameHeight/60;
                positionX = baseFrameWidth/2 - bufferedImage.getWidth()/2;
                break;
            case "bottomLeft":
                positionY = baseFrameHeight - bufferedImage.getHeight() - baseFrameHeight/60;
                positionX = baseFrameWidth/60;
                break;
            case "bottomRight":
                positionY = baseFrameHeight - bufferedImage.getHeight() - baseFrameHeight/60;
                positionX = baseFrameWidth - bufferedImage.getWidth() - baseFrameWidth/60;
                break;
            case "top":
                positionY = bufferedImage.getHeight()/3;
                positionX = baseFrameWidth/2 - bufferedImage.getWidth()/2;
                break;
            case "topLeft":
                positionY = baseFrameHeight/20;
                positionX = baseFrameWidth/60;
                break;
            case "topRight":
                positionY = baseFrameHeight/20;
                positionX = baseFrameWidth - bufferedImage.getWidth() - baseFrameWidth/60;
                break;
        }
    }

    /**
     * Set position of the item on the base frame
     * @param coordinates coordinates like [40,10]
     */
    public void setPosition(List<Integer> coordinates) {
        positionX = coordinates.get(0);
        positionY = coordinates.get(1);
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

}
