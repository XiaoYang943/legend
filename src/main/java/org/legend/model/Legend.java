package org.legend.model;

import java.awt.image.BufferedImage;

public class Legend {

    static int positionX;
    static int positionY;

    public void setPosition(String position, BufferedImage image, BufferedImage legendBufferedImage) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getWidth();
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
                positionX = imgWidth - imgWidth/5;
                break;
            case "top":
                positionY = legendBufferedImage.getHeight()/2;
                positionX = imgWidth/2 - legendBufferedImage.getWidth()/2;
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
