package org.legend.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Legend {

    int x;

    int y;

    int width;

    int height;

    Color backgroundColor = Color.WHITE;

    String title = "Legend";

    Font titleFont = new Font("Arial", Font.BOLD, 18);

    Color titleColor = Color.BLACK;

    Font textFont = new Font("Arial", Font.PLAIN, 12);

    Color textColor = Color.BLACK;

    List<LegendItem> legendItem = new ArrayList<>();

    int gapBetweenLegendItem = 10;

    String numberFormat = "#.##";

    /**
     * Create a Legend from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    Legend(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Set the background color
     * @param backgroundColor The background color
     */
    void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Set the legend title
     * @param title The title
     */
    void setTitle(String title) {
        this.title = title;
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
     * Set the text Font
     * @param textFont The text Font
     */
    void setTextFont(Font textFont) {
        this.textFont = textFont;
    }

    /**
     * Set the text Color
     * @param textColor The text Color
     */
    void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    /**
     * Set the gap between entries
     * @param gapBetweenEntries The gap between entries
     */
    void setGapBetweenEntries(int gapBetweenEntries) {
        this.gapBetweenLegendItem = gapBetweenEntries;
    }

    /**
     * Set the number format
     * @param numberFormat The number format
     */
    void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

}
