package org.legend.model;

import java.awt.image.BufferedImage;

abstract public class Item {

    int positionX;
    int positionY;

    /**
     * Set position of the item on the base frame
     * @param position the position (can be absolute position like "[40:10]")
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
