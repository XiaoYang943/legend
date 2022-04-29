package org.legend.model;

import org.geotools.map.MapContent;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class Title {

    String title;
    Color titleColor;
    int titleFont;
    int titleSize;
    String titleFontName;
    boolean underlined;
    int positionX;
    int positionY;

    public Title(String title, Color titleColor, int titleFont, int titleSize, String titleFontName, boolean underlined){
        this.title = title;
        this.titleColor = titleColor;
        this.titleFont = titleFont;
        this.titleSize = titleSize;
        this.titleFontName = titleFontName;
        this.underlined = underlined;
    }

    public BufferedImage paintTitle(){
        BufferedImage titleBufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graph2d = titleBufferedImage.createGraphics();
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0, 0, 200, 200);
        graph2d.setPaint(titleColor);
        graph2d.setFont(new Font(titleFontName, titleFont, titleSize));
        int titleWidth = graph2d.getFontMetrics().stringWidth(title);
        graph2d.drawString(title, titleBufferedImage.getWidth()/2 - titleWidth/2 , 20);
        if(underlined){
            graph2d.draw( new Line2D.Double( (double) titleBufferedImage.getWidth()/2 - (double) titleWidth/2, 30, (double) titleBufferedImage.getWidth()/2 + (double) titleWidth/2, 30 ) );
        }
        return titleBufferedImage;
    }

    public void setPosition(String position, BufferedImage image, BufferedImage scaleBufferedImage) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
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
                positionY = imgHeight/20;
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
