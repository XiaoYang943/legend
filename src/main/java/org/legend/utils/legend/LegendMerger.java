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

package org.legend.utils.legend;


import org.geotools.api.style.Rule;
import org.legend.options.LegendOptions;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.legend.utils.legend.LegendUtils.*;

/**
 * Utility class containing static methods for merging single legend elements into a final output,
 * following a set of given layout options / constraints.
 * Comes from the package org.geoserver.wms.legendgraphic
 *
 * @author @author Adrien Bessy (entirely based on what mauro.bartolomeoli@geo-solutions.it did with geoserver)
 */
public class LegendMerger {

    /**
     * Set of options for legend merging. Used to set all needed options for merging a set of icons in a single place
     *
     * @author mauro.bartolomeoli@geo-solutions.it
     */
    public static class MergeOptions {
        List<RenderedImage> imageStack;
        int margin;
        Color backgroundColor;
        boolean transparent;
        boolean antialias;
        LegendUtils.LegendLayout layout;
        int rowWidth;
        int rows;
        int columns;
        Font labelFont;
        boolean forceLabelsOn;
        boolean forceLabelsOff;
        boolean forceTitlesOff;

        /**
         * Build a new set of options, specifying each option.
         *
         * @param imageStack      images representing the icons to merge
         * @param dx              horizontal dimension for raster icons
         * @param dy              vertical dimension for raster icons
         * @param margin          margin between icons
         * @param backgroundColor background color for the merged image
         * @param transparent     using a transparent background
         * @param antialias       enable antialiasing of fonts in labels
         * @param layout          layout to be used (vertical, horizontal)
         * @param rowWidth        rowwidth parameter (for horizontal layout)
         * @param rows            # of rows (for horizontal layout)
         * @param columnHeight    columnheight parameter (for vertical layout)
         * @param columns         # of columns (for vertical layout)
         * @param labelFont       font to be used for labels
         * @param forceLabelsOn   force labels to be always rendered
         * @param forceLabelsOff  force labels to be never rendered
         */
        public MergeOptions(
                List<RenderedImage> imageStack,
                int margin,
                Color backgroundColor,
                boolean transparent,
                boolean antialias,
                LegendUtils.LegendLayout layout,
                int rowWidth,
                int rows,
                int columns,
                Font labelFont,
                boolean forceLabelsOn,
                boolean forceLabelsOff, LegendOptions legendOptions) {
            super();
            this.imageStack = imageStack;
            this.margin = margin;
            this.backgroundColor = backgroundColor;
            this.transparent = transparent;
            this.antialias = antialias;
            this.layout = layout;
            this.rowWidth = rowWidth;
            this.rows = rows;
            this.columns = columns;
            this.labelFont = labelFont;
            this.forceLabelsOn = forceLabelsOn;
            this.forceLabelsOff = forceLabelsOff;
        }

        /**
         * Build a new set of options, getting most of the options from a legendOptions map.
         *
         * @param imageStack     images representing the icons to merge
         * @param dx             horizontal dimension for raster icons
         * @param dy             vertical dimension for raster icons
         * @param margin         margin between icons
         * @param legendOptions  legendOptions map
         * @param forceLabelsOn  force labels to be always rendered
         * @param forceLabelsOff force labels to be never rendered
         */
        public MergeOptions(
                List<RenderedImage> imageStack,
                int margin,
                boolean forceLabelsOn,
                boolean forceLabelsOff,
                LegendOptions legendOptions) {
            this(
                    imageStack,
                    margin,
                    LegendUtils.getBackgroundColor(legendOptions),
                    false,
                    true,
                    legendOptions.getLayout(),
                    DEFAULT_ROW_WIDTH,
                    DEFAULT_ROWS,
                    DEFAULT_COLUMNS,
                    LegendUtils.getLabelFont(legendOptions),
                    forceLabelsOn,
                    forceLabelsOff, legendOptions);
        }

        public List<RenderedImage> getImageStack() {
            return imageStack;
        }


        public int getMargin() {
            return margin;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }

        public boolean isTransparent() {
            return transparent;
        }

        public boolean isAntialias() {
            return antialias;
        }

        public LegendUtils.LegendLayout getLayout() {
            return layout;
        }

        public int getRowWidth() {
            return rowWidth;
        }

        public int getRows() {
            return rows;
        }

        public int getColumns() {
            return columns;
        }

        public Font getLabelFont() {
            return labelFont;
        }

        public boolean isForceLabelsOn() {
            return forceLabelsOn;
        }

        public boolean isForceLabelsOff() {
            return forceLabelsOff;
        }

        public boolean isForceTitlesOff() {
            return forceTitlesOff;
        }

        public static MergeOptions createFromOptions(
                List<RenderedImage> imageStack,
                int margin,
                boolean forceLabelsOn,
                boolean forceLabelsOff, LegendOptions legendOptions) {
            return new LegendMerger.MergeOptions(
                    imageStack,
                    margin,
                    forceLabelsOn,
                    forceLabelsOff, legendOptions);
        }
    }

    /**
     * Receives a list of <code>BufferedImages</code>, embedded in the mergeOptions object, and
     * produces a new one which holds all the images in <code>imageStack</code> one above the other,
     * handling labels.
     *
     * @param rules         The applicable rules, one for each image in the stack (if not null it's used to compute labels)
     * @param legendOptions The legend options.
     * @param mergeOptions  options to be used for merging
     * @return the image with all the images on the argument list.
     */
    public static BufferedImage mergeLegends(
            Rule[] rules, MergeOptions mergeOptions, LegendOptions legendOptions) throws Exception {
        List<RenderedImage> imageStack = mergeOptions.getImageStack();

        // Builds legend nodes (graphics + label)
        final int imgCount = imageStack.size();
        List<BufferedImage> nodes = new ArrayList<>();
        // Single legend, no rules, no force label
        if (imgCount == 1 && (!mergeOptions.isForceLabelsOn() || rules == null)) {
            return (BufferedImage) imageStack.get(0);
        } else {
            for (int i = 0; i < imgCount; i++) {
                BufferedImage img = (BufferedImage) imageStack.get(i);
                if (rules != null && rules[i] != null) {
                    BufferedImage label = renderLabel(img, rules[i], mergeOptions, legendOptions);
                    if (label != null) {
                        int ruleOffsetY = legendOptions.getRuleOffsetY();
                        if (i == 0) {
                            ruleOffsetY = 0;
                        }
                        img = joinBufferedImageHorizontally(
                                img,
                                label,
                                mergeOptions.getLabelFont(),
                                mergeOptions.isAntialias(),
                                mergeOptions.isTransparent(),
                                mergeOptions.getBackgroundColor(),
                                legendOptions.getLabelOffsetX(),
                                ruleOffsetY);
                    }
                    nodes.add(img);
                } else {
                    nodes.add(img);
                }
            }
        }

        // Sets legend nodes into a matrix according to layout rules
        LegendUtils.LegendLayout layout = mergeOptions.getLayout();
        BufferedImage finalLegend = null;
        if (layout == LegendUtils.LegendLayout.HORIZONTAL) {
            Row[] rows = createRows(nodes, mergeOptions.getRowWidth(), mergeOptions.getRows());
            finalLegend = buildFinalHLegend(rows, mergeOptions);
        }

        if (layout == LegendUtils.LegendLayout.VERTICAL) {
            Column[] columns =
                    createColumns(
                            nodes,
                            mergeOptions.getColumns(),
                            true, legendOptions);
            finalLegend = buildFinalVLegend(columns, mergeOptions);
        }

        return finalLegend;
    }

    /**
     * Receives a list of <code>BufferedImages</code>, embedded in the mergeOptions object, and
     * produces a new one which holds all the images in <code>imageStack</code> one above the other,
     * handling labels.
     *
     * @param rules        The applicable rules, one for each image in the stack (if not null it's used to compute labels)
     * @param mergeOptions options to be used for merging
     * @return the image with all the images on the argument list.
     */
    public static BufferedImage mergeGroups(Rule[] rules, MergeOptions mergeOptions, LegendOptions legendOptions) throws Exception {
        List<RenderedImage> imageStack = mergeOptions.getImageStack();

        final int imgCount = imageStack.size();
        if (imgCount == 1 && (!mergeOptions.isForceLabelsOn() || rules == null)) {
            return (BufferedImage) imageStack.get(0);
        }

        List<BufferedImage> nodes = new ArrayList<>(imgCount / 2);

        if (mergeOptions.isForceTitlesOff()) {
            for (RenderedImage img : imageStack) {
                nodes.add((BufferedImage) img);
            }

        } else {
            // merge layer titles with legend images
            for (int i = 0; i < imgCount; i = i + 2) {
                BufferedImage lbl = (BufferedImage) imageStack.get(i);
                BufferedImage img = (BufferedImage) imageStack.get(i + 1);
                img = joinBufferedImageVertically(
                        lbl,
                        img,
                        mergeOptions.getLabelFont(),
                        mergeOptions.isAntialias(),
                        mergeOptions.isTransparent(),
                        mergeOptions.getBackgroundColor());
                nodes.add(img);
            }
        }

        // Sets legend nodes into a matrix according to layout rules
        LegendUtils.LegendLayout layout = mergeOptions.getLayout();
        BufferedImage finalLegend = null;
        if (layout == LegendUtils.LegendLayout.HORIZONTAL) {
            Row[] rows = createRows(nodes, 0, 0);
            finalLegend = buildFinalHLegend(rows, mergeOptions);
        }

        if (layout == LegendUtils.LegendLayout.VERTICAL) {
            Column[] columns = createColumns(nodes, 0, false, legendOptions);
            finalLegend = buildFinalVLegend(columns, mergeOptions);
        }

        return finalLegend;
    }

    /**
     * Represents a column of legends images
     */
    private static class Column {
        private int width;

        private int height;

        private final List<BufferedImage> nodes = new ArrayList<>();

        public void addNode(BufferedImage img) {
            nodes.add(img);
            width = Math.max(width, img.getWidth());
            height = height + img.getHeight();
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public List<BufferedImage> getNodes() {
            return nodes;
        }
    }

    /**
     * Represents a row of legends images
     */
    private static class Row {
        private int width;

        private int height;

        private final List<BufferedImage> nodes = new ArrayList<>();

        public void addNode(BufferedImage img) {
            nodes.add(img);
            height = Math.max(height, img.getHeight());
            width = width + img.getWidth();
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public List<BufferedImage> getNodes() {
            return nodes;
        }
    }

    /**
     * Creates non-raster legend columns for vertical layout according to max height and max columns
     * limits
     *
     * @param nodes         legend images
     * @param maxHeight     maximum height of legend
     * @param maxColumns    maximum number of columns
     * @param legendOptions general mechanism for acquiring legend symbols
     * @param checkColor    check for presence of color in legend
     * @return column list
     */
    private static Column[] createColumns(List<BufferedImage> nodes, int maxColumns,
                                          boolean checkColor, LegendOptions legendOptions) {
        Integer maxHeight = legendOptions.getMaxHeight();
        Column[] legendMatrix;
        /*
         * Limit max height
         */
        if (maxHeight > 0) {
            /*
             * Limit max column
             */
            int cnLimit = maxColumns > 0 ? maxColumns : nodes.size();
            legendMatrix = new Column[cnLimit];
            legendMatrix[0] = new Column();
            int cn = 0;
            int columnHeight = 0;
            for (int i = 0; i < nodes.size(); i++) {
                BufferedImage node = nodes.get(i);
                if (columnHeight <= maxHeight) {
                    // Fill current column
                    legendMatrix[cn].addNode(node);
                    columnHeight = columnHeight + node.getHeight();
                } else {
                    // Add current node to next column
                    i--;
                    cn++;
                    // Stop if column limits is reached
                    if (cn == cnLimit) {
                        break;
                    }
                    // Reset column counter
                    columnHeight = 0;
                    // Create new column
                    legendMatrix[cn] = new Column();
                }
            }
        } else {
            /*
             * Limit max column, if no limit set it to 1
             */
            int colNumber = maxColumns > 0 ? maxColumns : 1;
            legendMatrix = new Column[colNumber];
            legendMatrix[0] = new Column();
            int rowNumber = (int) Math.ceil((float) nodes.size() / colNumber);
            int cn = 0;
            int rc = 0;
            boolean colourPresent;
            for (int i = 0; i < nodes.size(); i++) {
                if (rc < rowNumber) {
                    if (checkColor) {
                        // check for presence of colour (ie. non-empty legend row)
                        colourPresent = checkColor(nodes.get(i), legendOptions);
                        if (colourPresent) {
                            legendMatrix[cn].addNode(nodes.get(i));
                            rc++;
                        }
                    } else {
                        legendMatrix[cn].addNode(nodes.get(i));
                        rc++;
                    }
                } else {
                    i--;
                    cn++;
                    rc = 0;
                    legendMatrix[cn] = new Column();
                }
            }
        }

        return legendMatrix;
    }

    /**
     * Checks the pixels for presence of colour against legend background
     *
     * @param img           given row of the legend
     * @param legendOptions general mechanism for acquiring legend symbols
     * @return false if no colours are detected
     */
    public static boolean checkColor(BufferedImage img, LegendOptions legendOptions) {
        int w = img.getWidth();
        int h = img.getHeight();
        boolean colourPresent = false;
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                if (img.getRGB(j, k) != LegendUtils.getBackgroundColor(legendOptions).getRGB()) {
                    colourPresent = true;
                }
            }
        }
        return colourPresent;
    }

    /**
     * Creates legends rows for horizontal layout according to max width and max rows limits
     *
     * @param nodes    legend images
     * @param maxWidth maximum width of legend
     * @param maxRows  maximum number of rows
     * @return row list
     */
    private static Row[] createRows(List<BufferedImage> nodes, int maxWidth, int maxRows) {
        Row[] legendMatrix;
        /*
         * Limit max height
         */
        if (maxWidth > 0) {
            /*
             * Limit max column
             */
            int rnLimit = maxRows > 0 ? maxRows : nodes.size();
            legendMatrix = new Row[rnLimit];
            legendMatrix[0] = new Row();
            int rn = 0;
            int rowWidth = 0;
            for (int i = 0; i < nodes.size(); i++) {
                BufferedImage node = nodes.get(i);
                if (rowWidth <= maxWidth) {
                    // Fill current column
                    legendMatrix[rn].addNode(node);
                    rowWidth = rowWidth + node.getWidth();
                } else {
                    // Add current node to next column
                    i--;
                    rn++;
                    // Stop if column limits is reached
                    if (rn == rnLimit) {
                        break;
                    }
                    // Reset column counter
                    rowWidth = 0;
                    // Create new column
                    legendMatrix[rn] = new Row();
                }
            }
        } else {
            /*
             * Limit max column, if no limit set it to 1
             */
            int rowNumber = maxRows > 0 ? maxRows : 1;
            legendMatrix = new Row[rowNumber];
            legendMatrix[0] = new Row();
            int colNumber = (int) Math.ceil((float) nodes.size() / rowNumber);
            int rn = 0;
            int cc = 0;
            for (int i = 0; i < nodes.size(); i++) {
                if (cc < colNumber) {
                    legendMatrix[rn].addNode(nodes.get(i));
                    cc++;
                } else {
                    i--;
                    rn++;
                    cc = 0;
                    legendMatrix[rn] = new Row();
                }
            }
        }

        return legendMatrix;
    }

    /**
     * Renders legend columns and cut off the node that exceeds the maximum limits
     *
     * @param columns list of columns to draw
     * @param options options to be used for merging
     * @return BufferedImage of legend
     */
    private static BufferedImage buildFinalVLegend(Column[] columns, MergeOptions options) throws Exception {

        int totalWidth = 0;
        int totalHeight = 0;

        for (Column c : columns) {
            if (c != null) {
                totalWidth = totalWidth + c.getWidth();
                int h = c.getHeight();
                totalHeight = Math.max(totalHeight, h);
            }
        }
        totalWidth = totalWidth + options.getMargin() * 2;
        totalHeight = totalHeight + options.getMargin() * 2;
        // buffer the width a bit
        totalWidth += 2;
        final BufferedImage finalLegend = ImageUtils.createImage(totalWidth, totalHeight, null, options.isTransparent());
        final Map<RenderingHints.Key, Object> hintsMap = new HashMap<>();
        Graphics2D finalGraphics = ImageUtils.prepareTransparency(
                options.isTransparent(),
                options.getBackgroundColor(),
                finalLegend,
                hintsMap);
        // finalGraphics.setFont(labelFont);
        if (options.isAntialias()) {
            finalGraphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            finalGraphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        int vOffset = options.getMargin();
        int hOffset = options.getMargin();
        for (Column c : columns) {
            if (c != null) {
                for (BufferedImage n : c.getNodes()) {
                    finalGraphics.drawImage(n, hOffset, vOffset, null);
                    vOffset = vOffset + n.getHeight();
                }
                hOffset = hOffset + c.getWidth();
                vOffset = options.getMargin();
            }
        }
        finalGraphics.dispose();

        return finalLegend;
    }

    /**
     * Renders legend rows and cut off the node that exceeds the maximum limits
     *
     * @param rows    list of rows to draw
     * @param options options to be used for merging
     * @return BufferedImage of legend
     */
    private static BufferedImage buildFinalHLegend(Row[] rows, MergeOptions options) throws Exception {

        int totalWidth = 0;
        int totalHeight = 0;

        for (Row r : rows) {
            if (r != null) {
                if (totalHeight > 0) {
                    totalHeight = totalHeight;
                }
                totalHeight = totalHeight + r.getHeight();
                int w = r.getWidth();
                totalWidth = Math.max(totalWidth, w);
            }
        }
        totalWidth = totalWidth + options.getMargin() * 2;
        totalHeight = totalHeight + options.getMargin() * 2;
        // buffer the width a bit
        totalWidth += 2;
        final BufferedImage finalLegend =
                ImageUtils.createImage(totalWidth, totalHeight, null, options.isTransparent());
        final Map<RenderingHints.Key, Object> hintsMap = new HashMap<>();
        Graphics2D finalGraphics =
                ImageUtils.prepareTransparency(
                        options.isTransparent(),
                        options.getBackgroundColor(),
                        finalLegend,
                        hintsMap);
        if (options.isAntialias()) {
            finalGraphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            finalGraphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        int vOffset = options.getMargin();
        int hOffset = options.getMargin();
        for (Row r : rows) {
            if (r != null) {
                for (BufferedImage n : r.getNodes()) {
                    finalGraphics.drawImage(n, hOffset, vOffset, null);
                    hOffset = hOffset + n.getWidth();
                }
                vOffset = vOffset + r.getHeight();
                hOffset = options.getMargin();
            }
        }
        finalGraphics.dispose();

        return finalLegend;
    }

    /**
     * Join image and label to create a single legend node image horizontally
     *
     * @param img             image of legend
     * @param label           label of legend
     * @param labelFont       font to use
     * @param useAA           if true applies anti aliasing
     * @param transparent     if true make legend transparent
     * @param backgroundColor background color of legend
     * @return BufferedImage of image and label side by side and vertically center
     */
    private static BufferedImage joinBufferedImageHorizontally(
            BufferedImage img,
            BufferedImage label,
            Font labelFont,
            boolean useAA,
            boolean transparent,
            Color backgroundColor,
            int labelXOffset, int ruleOffsetY) throws Exception {
        // do some calculate first
        int wid = img.getWidth() + label.getWidth() + labelXOffset;
        int height = Math.max(img.getHeight(), label.getHeight()) + ruleOffsetY;
        // create a new buffer and draw two image into the new image
        BufferedImage newImage = ImageUtils.createImage(wid, height, null, transparent);
        final Map<RenderingHints.Key, Object> hintsMap = new HashMap<>();
        Graphics2D g2 =
                ImageUtils.prepareTransparency(transparent, backgroundColor, newImage, hintsMap);
        g2.setFont(labelFont);
        if (useAA) {
            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        // move the images to the vertical center of the row
        int imgOffset = (int) Math.round((height - img.getHeight()) / 2d);
        int labelYOffset = (int) Math.round((height - label.getHeight()) / 2d);
        g2.drawImage(img, null, 0, imgOffset);
        g2.drawImage(label, null, img.getWidth() + labelXOffset, labelYOffset);
        g2.dispose();
        return newImage;
    }

    /**
     * Join image and label to create a single legend node image vertically
     *
     * @param img             image of legend
     * @param label           label of legend
     * @param labelFont       font to use
     * @param useAA           if true applies anti aliasing
     * @param transparent     if true make legend transparent
     * @param backgroundColor background color of legend
     * @return BufferedImage of image and label side by side and vertically center
     */
    private static BufferedImage joinBufferedImageVertically(
            BufferedImage label,
            BufferedImage img,
            Font labelFont,
            boolean useAA,
            boolean transparent,
            Color backgroundColor) throws Exception {
        // do some calculate first
        int offset = 0;
        int height = img.getHeight() + label.getHeight() + offset;
        int wid = Math.max(img.getWidth(), label.getWidth()) + offset;
        // create a new buffer and draw two image into the new image
        BufferedImage newImage = ImageUtils.createImage(wid, height, null, transparent);
        final Map<RenderingHints.Key, Object> hintsMap = new HashMap<>();
        Graphics2D g2 =
                ImageUtils.prepareTransparency(transparent, backgroundColor, newImage, hintsMap);
        g2.setFont(labelFont);
        if (useAA) {
            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        // move the images to the vertical center of the row
        g2.drawImage(label, null, 0, 0);
        g2.drawImage(img, null, 0, label.getHeight());
        g2.dispose();
        return newImage;
    }

    /**
     * Renders the legend image label
     *
     * @param img           the BufferedImage
     * @param rule          the applicable rule for img, if rule is not null the label will be rendered
     * @param legendOptions the legend options
     * @param options       options to be used for merging
     * @return the BufferedImage of label
     */
    private static BufferedImage renderLabel(
            RenderedImage img, Rule rule, MergeOptions options, LegendOptions legendOptions) {
        BufferedImage labelImg = null;
        if (!options.isForceLabelsOff() && rule != null) {
            String label = LegendUtils.getRuleLabel(rule);
            if (label != null && label.length() > 0) {
                labelImg = getRenderedLabel((BufferedImage) img, label, legendOptions);
            }
        }
        return labelImg;
    }

    /**
     * Renders a label on the given image, using parameters from the options for the rendering
     * style.
     *
     * @param image         the BufferedImage
     * @param label         the label
     * @param legendOptions the legend options
     * @return a customized label image
     */
    public static BufferedImage getRenderedLabel(BufferedImage image, String label, LegendOptions legendOptions) {
        final Graphics2D graphics = image.createGraphics();
        Font labelFont = LegendUtils.getLabelFont(legendOptions);
        graphics.setFont(labelFont);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return LegendUtils.renderLabel(label, graphics, legendOptions);
    }
}

