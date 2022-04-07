package org.legend.model;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO
public class Legend  extends Layer {

    int xPosition;
    int yPosition;
    BufferedImage bufferedImage;
    String title = "Legend";
    Font titleFont = new Font("Arial", Font.BOLD, 18);
    Color titleColor = Color.BLACK;
    String titleStyle = "Sans-Serif";


    /**
     * Create a Legend from the top left.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param bufferedImage The legend graphic buffered image
     */
    public Legend(int x, int y, BufferedImage bufferedImage) {
        this.xPosition = x;
        this.yPosition = y;
        this.bufferedImage = bufferedImage;
    }

    /**
     * Set the title Font
     * @param titleFont The title Font
     */
    void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
    }

    /**
     * Set the title Color
     * @param titleColor The title Color
     */
    void setTitleColor(Color titleColor) {
        this.titleColor = titleColor;
    }

    /**
     * Set the title style
     * @param titleStyle The title Style
     */
    void setTitleStyle(String titleStyle) {
        this.titleStyle = titleStyle;
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return null;
    }
}
