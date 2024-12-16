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

package org.legend.utils;

import org.apache.commons.lang3.StringUtils;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.style.*;
import org.geotools.api.util.InternationalString;
import org.geotools.factory.CommonFactoryFinder;
import org.legend.options.LegendOptions;

import java.awt.Font;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Utility class for building legends, it exposes many methods that could be reused anywhere.
 *
 * <p>I am not preventing people from subclassing this method so that they could add their own
 * utility methods.
 * Comes from the package org.geoserver.wms.legendgraphic
 *
 * @author Adrien Bessy (entirely based on what Simone Giannecchini, GeoSolutions SAS did with GeoServer)
 */
public class LegendUtils {
    /**
     * Ensures that the provided argument is not <code>null</code>.
     *
     * <p>If it <code>null</code> it must throw a {@link NullPointerException}.
     *
     * @param argument argument to check for <code>null</code>.
     */
    protected static void ensureNotNull(final Object argument) {
        ensureNotNull(argument, "Argument cannot be null");
    }

    /**
     * Ensures that the provided argument is not <code>null</code>.
     *
     * <p>If it <code>null</code> it must throw a {@link NullPointerException}.
     *
     * @param argument argument to check for <code>null</code>.
     * @param message  leading message to print out in case the test fails.
     */
    protected static void ensureNotNull(final Object argument, final String message) {
        if (message == null) throw new NullPointerException("Message cannot be null");
        if (argument == null) throw new NullPointerException(message + " cannot be null");
    }

    /**
     * Legend layouts
     */
    public enum LegendLayout {
        HORIZONTAL,
        VERTICAL
    }

    /**
     * Default {@link Font} name for legends.
     */
    public static final String DEFAULT_FONT_NAME = "Sans-Serif";

    /**
     * Default {@link Font} for legends.
     */
    public static final int DEFAULT_FONT_TYPE = Font.PLAIN;

    /**
     * Default {@link Font} for legends.
     */
    public static final int DEFAULT_FONT_SIZE = 12;

    /**
     * Default {@link Font} for legends.
     */
    public static final Font DEFAULT_FONT = new Font("Sans-Serif", Font.PLAIN, 12);

    /**
     * Default Legend graphics background color
     */
    public static final Color DEFAULT_BG_COLOR = Color.WHITE;
    /**
     * Default label color
     */
    public static final Color DEFAULT_FONT_COLOR = Color.BLACK;

    /**
     * padding percentage factor at both sides of the legend.
     */
    public static final float hpaddingFactor = 0.15f;
    /**
     * top & bottom padding percentage factor for the legend
     */
    public static final float vpaddingFactor = 0.15f;


    /**
     * default row width is not limited
     */
    public static final int DEFAULT_ROW_WIDTH = 0;

    /**
     * default column number is not limited
     */
    public static final int DEFAULT_COLUMNS = 0;

    /**
     * default row number is not limited
     */
    public static final int DEFAULT_ROWS = 0;

    /**
     * shared package's logger
     */
    public static final Logger LOGGER = org.geotools.util.logging.Logging.getLogger(LegendUtils.class.getPackage().getName());

    /**
     * Tries to decode the provided {@link String} into an HEX color definition in RRGGBB, 0xRRGGBB
     * or #RRGGBB format
     *
     * <p>In case the {@link String} is not correct a {@link NumberFormatException} will be thrown.
     *
     * @param hex a {@link String} that should contain an Hexadecimal color representation.
     * @return a {@link Color} representing the provided {@link String}.
     * @throws NumberFormatException in case the string is badly formatted.
     */
    public static Color color(String hex) {
        ensureNotNull(hex, "hex value");
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }
        return Color.decode(hex);
    }

    /**
     * Finds the applicable Rules for the given scale denominator.
     *
     * @param ftStyles a featureTypeStyle list.
     * @return an array of {@link Rule}s.
     */
    public static Rule[] getRules(final FeatureTypeStyle[] ftStyles) {
        ensureNotNull(ftStyles, "FeatureTypeStyle array is null");
        final List<Rule> ruleList = new ArrayList<>();
        for (int j = 0; j < ftStyles[0].rules().size(); j++) {
            if (ftStyles[0].rules().get(j).toString().contains("if_then_else")) {
                StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
                if (ftStyles[0].rules().get(0).symbolizers().get(0) instanceof LineSymbolizer) {
                    System.out.println("ftStyles[0].rules().get(0).symbolizers().get(0) : " + ftStyles[0].rules().get(0).symbolizers().get(0));
                    Expression getWidth = ((LineSymbolizer) ftStyles[0].rules().get(0).symbolizers().get(0)).getStroke().getWidth();
                    String getColor = String.valueOf(((LineSymbolizer) ftStyles[0].rules().get(0).symbolizers().get(0)).getStroke().getColor());
                    String operator = null;
                    String variable2 = null;
                    String value = null;
                    List<String> widthList = null;
                    if (getWidth.toString().contains("if_then_else")) {
                        String width1 = StringUtils.substringBetween(String.valueOf(getWidth), ")], [", "], [");
                        String width2 = StringUtils.substringsBetween(String.valueOf(getWidth), "], [", "])")[1];
                        width2 = width2.replace(width1, "").replace("], [", "");
                        widthList = new ArrayList<>();
                        widthList.add(width1);
                        widthList.add(width2);
                        String variable = Arrays.toString(StringUtils.substringsBetween(String.valueOf(getWidth), "([", "],"));
                        operator = StringUtils.substringBetween(String.valueOf(getWidth), "[", "(");
                        variable2 = variable.replace(operator, "").replace("[([", "").replace("]", "");
                        value = StringUtils.substringBetween(String.valueOf(getWidth), ", [", "])],");
                    }
                    String[] colors = null;
                    if (getColor.contains("if_then_else")) {
                        colors = StringUtils.substringsBetween(getColor, "#", "]");
                        String variable = Arrays.toString(StringUtils.substringsBetween(getColor, "([", "],"));
                        operator = StringUtils.substringBetween(getColor, "[", "(");
                        variable2 = variable.replace(operator, "").replace("[([", "").replace("]", "");
                        value = StringUtils.substringBetween(getColor, ", [", "])],");
                    }
                    FilterFactory filterFactory2 = CommonFactoryFinder.getFilterFactory();
                    float LINE_WIDTH = 1.0f;
                    List<String> operatorList = new ArrayList<>();
                    assert operator != null;
                    if (operator.equals("greaterThan")) {
                        operatorList.add(">");
                        operatorList.add("<");
                    }
                    if (operator.equals("lowerThan")) {
                        operatorList.add("<");
                        operatorList.add(">");
                    }
                    for (int i = 0; i < 2; i++) {
                        Rule rule = styleFactory.createRule();
//                        Stroke stroke;
//                        if(getColor.contains("if_then_else")) {
//                            assert colors != null;
//                            stroke = styleFactory.createStroke(filterFactory2.literal("#" + colors[i]), filterFactory2.literal(LINE_WIDTH));
//                        }
//                        else{
//                            if(getWidth.toString().contains("if_then_else")){
//                                assert widthList != null;
//                                stroke = styleFactory.createStroke(filterFactory2.literal(getColor), filterFactory2.literal(widthList.get(i)));
//                            }
//                            else {
//                                stroke = styleFactory.createStroke(filterFactory2.literal(getColor), filterFactory2.literal(LINE_WIDTH));
//                            }
//                        }
//                        Symbolizer symbolizer = styleFactory.createLineSymbolizer(stroke, null);
//                        rule.symbolizers().add(symbolizer);
//                        rule.setName(variable2 + " " + operatorList.get(i) + " " + value);
                        ruleList.add(rule);
                    }
                }
            } else {
                ruleList.add(ftStyles[0].rules().get(j));
            }
        }
        return ruleList.toArray(new Rule[0]);
    }

    /**
     * Returns the image background color for the given option.
     *
     * @param legendOptionsParam from which we should extract the background color.
     * @return the Color for the hexadecimal value passed by legendOptionsParam, or the default background color if no bgcolor were passed.
     */
    public static Color getBackgroundColor(LegendOptions legendOptionsNew) {
        Object clr = legendOptionsNew.getBgColor();
        if (clr instanceof Color) {
            return (Color) clr;
        } else if (clr == null) {
            return DEFAULT_BG_COLOR;
        }
        try {
            return color((String) clr);
        } catch (NumberFormatException e) {
            LOGGER.warning(
                    "Could not decode background color: "
                            + clr
                            + ", default to "
                            + DEFAULT_BG_COLOR.toString());
            return DEFAULT_BG_COLOR;
        }
    }

    /**
     * Returns the rule label.
     *
     * @param rule the given rule of a style.
     * @return the label of the rule.
     */
    static String getRuleLabel(Rule rule) {
        final Description description = rule.getDescription();
        String label = "";
        if (description != null && description.getTitle() != null) {
            final InternationalString title = description.getTitle();
            label = title.toString();
        } else if (rule.getName() != null) {
            label = rule.getName();
        }
        // 删除多余空格
        label = label.trim();
        return label;
    }

    /**
     * Extracts the Label font color from legendOptionsParam.
     *
     * <p>If there is no label font specified a default font color will be provided.
     *
     * @param legendOptionsParam the legendOptionsParam from which to extract label color information.
     * @return the Label font color extracted from the provided legendOptionsParam or a default font color.
     */
    public static Color getLabelFontColor(LegendOptions legendOptionsNew) {
        Object clr = legendOptionsNew.getFontColor();
        if (clr instanceof Color) {
            return (Color) clr;
        } else if (clr == null) {
            return DEFAULT_FONT_COLOR;
        }
        try {
            return color((String) clr);
        } catch (NumberFormatException e) {
            LOGGER.warning(
                    "Could not decode label color: "
                            + clr
                            + ", default to "
                            + DEFAULT_FONT_COLOR.toString());
            return DEFAULT_FONT_COLOR;
        }
    }


    /**
     * Return a {@link BufferedImage} representing this label. The characters '\n' '\r' and '\f' are
     * interpreted as line breaks, as is the character combination "\n" (as opposed to the actual
     * '\n' character). This allows people to force line breaks in their labels by including the
     * character "\" followed by "n" in their label.
     *
     * @param label              - the label to render
     * @param g                  - the Graphics2D that will be used to render this label
     * @param legendOptionsParam - the legend option param
     * @return a {@link BufferedImage} of the properly rendered label.
     */
    public static BufferedImage renderLabel(String label, final Graphics2D g, LegendOptions legendOptionsNew) {
        ensureNotNull(label);
        ensureNotNull(g);
        // We'll accept '/n' as a text string
        // to indicate a line break, as well as a traditional 'real' line-break in the XML.
        BufferedImage renderedLabel;
        Color labelColor = getLabelFontColor(legendOptionsNew);
        if ((label.contains("\n")) || (label.contains("\\n"))) {
            // this is a label WITH line-breaks...we need to figure out it's height *and*
            // width, and then adjust the legend size accordingly
            Rectangle2D bounds = new Rectangle2D.Double(0, 0, 0, 0);
            ArrayList<Integer> lineHeight = new ArrayList<>();
            // four backslashes... "\\" -> '\', so "\\\\n" -> '\' + '\' + 'n'
            final String realLabel = label.replaceAll("\\\\n", "\n");
            StringTokenizer st = new StringTokenizer(realLabel, "\n\r\f");

            while (st.hasMoreElements()) {
                final String token = st.nextToken();
                Rectangle2D thisLineBounds = g.getFontMetrics().getStringBounds(token, g);

                // if this is directly added as thisLineBounds.getHeight(), then there are rounding
                // errors
                // because we can only DRAW fonts at discrete integer coords.
                final int thisLineHeight = (int) Math.ceil(thisLineBounds.getHeight());
                bounds.add(0, thisLineHeight + bounds.getHeight());
                bounds.add(thisLineBounds.getWidth(), 0);
                lineHeight.add((int) Math.ceil(thisLineBounds.getHeight()));
            }

            // make the actual label image
            renderedLabel =
                    new BufferedImage(
                            (int) Math.ceil(bounds.getWidth()),
                            (int) Math.ceil(bounds.getHeight()),
                            BufferedImage.TYPE_INT_ARGB);

            st = new StringTokenizer(realLabel, "\n\r\f");

            Graphics2D rlg = renderedLabel.createGraphics();
            rlg.setColor(labelColor);
            rlg.setFont(g.getFont());
            rlg.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
            rlg.setRenderingHint(
                    RenderingHints.KEY_FRACTIONALMETRICS,
                    g.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
            int y = -g.getFontMetrics().getDescent();
            int c = 0;

            while (st.hasMoreElements()) {
                y += lineHeight.get(c++);
                rlg.drawString(st.nextToken(), 0, y);
            }
            rlg.dispose();
        } else {
            // this is a traditional 'regular-old' label.  Just figure the
            // size and act accordingly.
            int height = (int) Math.ceil(g.getFontMetrics().getStringBounds(label, g).getHeight());
            int width = (int) Math.ceil(g.getFontMetrics().getStringBounds(label, g).getWidth());
            renderedLabel = new BufferedImage(width + legendOptionsNew.getLabelXposition(), height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D rlg = renderedLabel.createGraphics();
            rlg.setColor(labelColor);
            rlg.setFont(g.getFont());
            rlg.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
            if (g.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS) != null) {
                rlg.setRenderingHint(
                        RenderingHints.KEY_FRACTIONALMETRICS,
                        g.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
            }
            rlg.drawString(label, legendOptionsNew.getLabelXposition(), height - rlg.getFontMetrics().getDescent());
            rlg.dispose();
        }

        return renderedLabel;
    }

    /**
     * Retrieves the font from the provided legendOptionsParam.
     *
     * @param legendOptionsParam a legendOptionsParam from which we should extract the {@link Font}
     *                           information.
     * @return the {@link Font} specified in the provided legendOptionsParam or a
     * default {@link Font}.
     */
    public static Font getLabelFont(LegendOptions legendOptionsNew) {

        String legendFontName = legendOptionsNew.getFontName();

        int legendFontFamily = LegendUtils.DEFAULT_FONT_TYPE;
        String legendFontFamily_ = legendOptionsNew.getFontStyle();
        if (legendFontFamily_.equalsIgnoreCase("italic")) {
            legendFontFamily = Font.ITALIC;
        } else if (legendFontFamily_.equalsIgnoreCase("bold")) {
            legendFontFamily = Font.BOLD;
        }

        int legendFontSize = legendOptionsNew.getFontSize();

        if (legendFontFamily == LegendUtils.DEFAULT_FONT_TYPE
                && legendFontName.equalsIgnoreCase(LegendUtils.DEFAULT_FONT_NAME)
                && (legendFontSize == LegendUtils.DEFAULT_FONT_SIZE || legendFontSize <= 0)) {
            return DEFAULT_FONT;
        }

        return new Font(legendFontName, legendFontFamily, legendFontSize);
    }
}
