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
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * Provides methods to build a title buffered image.
 *
 * @author Adrien Bessy
 */
public class TextItem extends Item{

    private final Font font;
    String content;
    Color titleColor;
    boolean underlined;

    public TextItem(String content, Color titleColor, Font font, boolean underlined){
        this.content = content;
        this.titleColor = titleColor;
        this.font = font;
        this.underlined = underlined;
    }

    /**
     * Paint a title and return a buffered image
     * @return the buffered image
     */
    public BufferedImage paintText(){
        BufferedImage titleBufferedImage = new BufferedImage(content.length()*3 + 200, font.getSize() + 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graph2d = titleBufferedImage.createGraphics();
        graph2d.setComposite(AlphaComposite.Clear);
        graph2d.fillRect(0, 0, content.length()*3 + 200, font.getSize() + 200);
        graph2d.setComposite(AlphaComposite.Src);
        graph2d.setPaint(titleColor);
        graph2d.setFont(font);
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int titleWidth = graph2d.getFontMetrics().stringWidth(content);
        if(content.length()>100){
            int i = 0;
            int y = 0;
            int sup = 0;
            while(i < content.length()){
                i = sup;
                sup = 75 + sup;
                if(sup > content.length()){
                    sup = content.length();
                }
                else {
                    for (int l = 0; l < 10; l++) {
                        if (content.substring(sup - 1 + l, sup + l).equals(" ")) {
                            sup = sup + l;
                            break;
                        }
                    }
                }
                graph2d.drawString(content.substring(i, sup), 0 , 20 + y);
                y = y + 20;
            }
        }
        else {
            graph2d.drawString(content, titleBufferedImage.getWidth() / 3 - titleWidth / 2, font.getSize()/2 + 20);
        }
        if(underlined){
            graph2d.draw( new Line2D.Double( (double) titleBufferedImage.getWidth()/3 - (double) titleWidth/2, font.getSize()/2.0 + 30, (double) titleBufferedImage.getWidth()/3 + (double) titleWidth/2, font.getSize()/2.0 + 30 ) );
        }
        return titleBufferedImage;
    }

}
