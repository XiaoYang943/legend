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
import java.util.List;

/**
 * Provides methods to build a base frame buffered image where a map and decorations can then be painted.
 *
 * @author Adrien Bessy
 */
public class MapDocument {

    int imgWidth;
    int imgHeight;
    private int margin;
    private BufferedImage baseFrameBufferedImage;

    /**
     * Set the size of the base image.
     * @param minWidth the minimal width
     * @param minHeight the minimal height
     * @param margin the margin
     */
    public void setSize(int minWidth, int minHeight, int margin) {
        this.imgWidth = minWidth + margin;
        this.imgHeight = minHeight + margin;
        this.margin = margin;
    }

    /**
     * Set the format of the base image.
     * @param landscape the format name
     */
    public void setBufferedImage(String landscape, int extraHeight, int extraWidth){
        switch (landscape) {
            case "LETTER_LANDSCAPE":
                if (imgHeight < 612) {
                    this.imgHeight = 612 + extraHeight;
                    this.imgWidth = 792 + extraWidth;
                } else {
                    this.imgWidth = imgHeight * 792 / 612 + extraWidth;
                    this.imgHeight = imgHeight + extraHeight;
                }
                break;
            case "TABLOID_LANDSCAPE":
                if (imgHeight < 792) {
                    this.imgHeight = 792 + extraHeight;
                    this.imgWidth = 1224 + extraWidth;
                } else {
                    this.imgWidth = imgHeight * 1224 / 792 + extraWidth;
                    this.imgHeight = imgHeight + extraHeight;
                }
                break;
            case "C_LANDSCAPE":
                if (imgHeight < 1224) {
                    this.imgHeight = 1224 + extraHeight;
                    this.imgWidth = 1584 + extraWidth;
                } else {
                    this.imgWidth = imgHeight * 1584 / 1224 + extraWidth;
                    this.imgHeight = imgHeight + extraHeight;
                }
                break;
            case "D_LANDSCAPE":
                if (imgHeight < 1584) {
                    this.imgHeight = 1584 + extraHeight;
                    this.imgWidth = 2448 + extraWidth;
                } else {
                    this.imgWidth = imgHeight * 2448 / 1584 + extraWidth;
                    this.imgHeight = imgHeight + extraHeight;
                }
                break;
            case "LETTER_PORTRAIT":
                if (imgWidth < 612) {
                    this.imgHeight = 792 + extraHeight;
                    this.imgWidth = 612 + extraWidth;
                } else {
                    this.imgHeight = imgWidth * 792 / 612 + extraHeight;
                    this.imgWidth = imgWidth + extraWidth;
                }
                break;
            case "TABLOID_PORTRAIT":
                if (imgWidth < 792) {
                    this.imgHeight = 1224 + extraHeight;
                    this.imgWidth = 792 + extraWidth;
                } else {
                    this.imgHeight = imgWidth * 1224 / 792 + extraHeight;
                    this.imgWidth = imgWidth + extraWidth;
                }
                break;
        }
        this.baseFrameBufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Paint map on the base frame, set its position and returns the graphics2d
     * @param position the position (can be absolute position like "[40:10]")
     * @param mapImage mapImage
     * @param g the graphics 2d
     * @return the graphics2D
     */
    public Graphics2D paintMap(String position, BufferedImage mapImage, Graphics2D g, Object bgColor) {
        if (g == null) {
            g = (Graphics2D) baseFrameBufferedImage.getGraphics();
            g.setPaint((Paint) bgColor);
            g.fillRect(0, 0, imgWidth, imgHeight);
        }
        if(margin > 0) {
            switch (position) {
                case "bottom":
                    g.drawImage(mapImage, (imgWidth - mapImage.getWidth())/2, imgHeight - mapImage.getHeight() - imgHeight/60, null);
                    break;
                case "right":
                    g.drawImage(mapImage, imgWidth - mapImage.getWidth() - imgWidth/60, (imgHeight - mapImage.getHeight())/2, null);
                    break;
                case "top":
                    g.drawImage(mapImage, margin / 2, imgHeight/60, null);
                    break;
                case "left":
                    g.drawImage(mapImage, imgWidth/60, (imgHeight - mapImage.getHeight())/2, null);
                    break;
                default: //center
                    g.drawImage(mapImage, (imgWidth - mapImage.getWidth())/2, (imgHeight - mapImage.getHeight())/2, null);
            }
        }else{
            g.drawImage(mapImage, 0, 0, null);
        }
        return g;
    }

    /**
     * Paint map on the base frame, set its position and returns the graphics2d
     * @param positionList the position (can be absolute position like "[40:10]")
     * @param mapImage mapImage
     * @param g the graphics 2d
     * @return the graphics2D
     */
    public Graphics2D paintMapOnMapDocument(List<Integer> positionList, BufferedImage mapImage, Graphics2D g, Object bgColor) {
        if (g == null) {
            g = (Graphics2D) baseFrameBufferedImage.getGraphics();

            g.setPaint((Paint) bgColor);
            g.fillRect(0, 0, imgWidth, imgHeight);
        }
        if(margin>0) {
                g.drawImage(mapImage, positionList.get(0), positionList.get(1), null);
        } else{
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
