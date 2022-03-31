/* (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.legend.legendGraphicDetails;

import org.apache.commons.text.WordUtils;
import org.geotools.renderer.i18n.ErrorKeys;
import org.geotools.renderer.i18n.Errors;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.style.ExpressionExtractor;
import org.geotools.styling.*;
import org.geotools.util.SuppressFBWarnings;
import org.legend.imageBuilder.LegendGraphicBuilder;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.style.ChannelSelection;
import org.opengis.util.InternationalString;

import java.awt.Font;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for building legends, it exposes many methods that could be reused anywhere.
 *
 * <p>I am not preventin people from subclassing this method so that they could add their own
 * utility methods.
 *
 * @author Simone Giannecchini, GeoSolutions SAS
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
     * @param message leading message to print out in case the test fails.
     */
    protected static void ensureNotNull(final Object argument, final String message) {
        if (message == null) throw new NullPointerException("Message cannot be null");
        if (argument == null) throw new NullPointerException(message + " cannot be null");
    }

    /** Legend layouts */
    public enum LegendLayout {
        HORIZONTAL,
        VERTICAL
    }

    public enum VAlign {
        TOP,
        MIDDLE,
        BOTTOM
    }

    public enum HAlign {
        LEFT,
        CENTERED,
        RIGHT,
        JUSTIFIED
    }

    /** Default {@link Font} name for legends. */
    public static final String DEFAULT_FONT_NAME = "Sans-Serif";

    /** Default {@link Font} for legends. */
    public static final int DEFAULT_FONT_TYPE = Font.PLAIN;

    /** Default {@link Font} for legends. */
    public static final int DEFAULT_FONT_SIZE = 12;

    /** Default {@link Font} for legends. */
    public static final Font DEFAULT_FONT = new Font("Sans-Serif", Font.PLAIN, 12);

    /** Default Legend graphics background color */
    public static final Color DEFAULT_BG_COLOR = Color.WHITE;
    /** Default label color */
    public static final Color DEFAULT_FONT_COLOR = Color.BLACK;

    /** padding percentage factor at both sides of the legend. */
    public static final float hpaddingFactor = 0.15f;
    /** top & bottom padding percentage factor for the legend */
    public static final float vpaddingFactor = 0.15f;

    /** default legend graphic layout is vertical */
    private static final LegendLayout DEFAULT_LAYOUT = LegendLayout.VERTICAL;

    /** default column height is not limited */
    private static final int DEFAULT_COLUMN_HEIGHT = 0;

    /** default row width is not limited */
    private static final int DEFAULT_ROW_WIDTH = 0;

    /** default column number is not limited */
    private static final int DEFAULT_COLUMNS = 0;

    /** default row number is not limited */
    private static final int DEFAULT_ROWS = 0;

    /** shared package's logger */
    private static final Logger LOGGER =
            org.geotools.util.logging.Logging.getLogger(LegendUtils.class.getPackage().getName());

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
     * @return an array of {@link Rule}s.
     */
    public static Rule[] getApplicableRules(
            final FeatureTypeStyle[] ftStyles, double scaleDenominator) {
        ensureNotNull(ftStyles, "FeatureTypeStyle array ");
        /** Holds both the rules that apply and the ElseRule's if any, in the order they appear */
        final List<Rule> ruleList = new ArrayList<>();

        // get applicable rules at the current scale
        for (FeatureTypeStyle fts : ftStyles) {
            for (Rule r : fts.rules()) {
                if (isWithInScale(r, scaleDenominator)) {
                    ruleList.add(r);

                    /*
                    * I'm commented this out since I guess it has no sense
                    * for producing the legend, since whether or not the rule
                    * has an else filter, the legend is drawn only if the
                    * scale denominator lies inside the rule's scale range.
                             if (r.hasElseFilter()) {
                                 ruleList.add(r);
                             }
                    */
                }
            }
        }

        return ruleList.toArray(new Rule[ruleList.size()]);
    }

    /**
     * Checks if a rule can be triggered at the current scale level
     *
     * @param r The rule
     * @param scaleDenominator the scale denominator to check if it is between the rule's scale
     *     range. -1 means that it allways is.
     * @return true if the scale is compatible with the rule settings
     */
    public static boolean isWithInScale(final Rule r, final double scaleDenominator) {
        return (scaleDenominator == -1)
                || (((r.getMinScaleDenominator() - LegendGraphicBuilder.TOLERANCE)
                <= scaleDenominator)
                && ((r.getMaxScaleDenominator() + LegendGraphicBuilder.TOLERANCE)
                > scaleDenominator));
    }

    /**
     * Checks if the graphics should be text antialiasing
     *
     * @param legendOptionsParam the {@link GetLegendGraphicRequest} from which to extract font antialiasing
     *     information.
     * @return true if the fontAntiAliasing is set to on
     */
    public static boolean isFontAntiAliasing(final Map<String, Object> legendOptionsParam) {
        if (legendOptionsParam.get("fontAntiAliasing") instanceof String) {
            String aaVal = (String) legendOptionsParam.get("fontAntiAliasing");
            if (aaVal.equalsIgnoreCase("on")
                    || aaVal.equalsIgnoreCase("true")
                    || aaVal.equalsIgnoreCase("yes")
                    || aaVal.equalsIgnoreCase("1")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the image background color for the given {@link GetLegendGraphicRequest}.
     *
     * @param legendOptionsParam a {@link GetLegendGraphicRequest} from which we should extract the background
     *     color.
     * @return the Color for the hexadecimal value passed as the <code>BGCOLOR</code> {@link
     *     GetLegendGraphicRequest#getLegendOptions() legend option}, or the default background
     *     color if no bgcolor were passed.
     */
    public static Color getBackgroundColor(final Map<String, Object> legendOptionsParam) {
        ensureNotNull(legendOptionsParam, "GetLegendGraphicRequestre");
        final Map<String, Object> legendOptions = legendOptionsParam;
        if (legendOptions == null) return DEFAULT_BG_COLOR;
        Object clr = legendOptions.get("bgColor");
        if (clr instanceof Color) {
            return (Color) clr;
        } else if (clr == null) {
            // return the default
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

    static String getRuleLabel(Rule rule) {
        // What's the label on this rule? We prefer to use
        // the 'title' if it's available, but fall-back to 'name'
        final Description description = rule.getDescription();

        String label = "";
        if (description != null && description.getTitle() != null) {
            final InternationalString title = description.getTitle();
            label = title.toString();
        } else if (rule.getName() != null) {
            label = rule.getName();
        }
        return label;
    }

    /**
     * Extracts the Label {@link Font} {@link Color} from the provided {@link
     * GetLegendGraphicRequest}.
     *
     * <p>If there is no label {@link Font} specified a default {@link Font} {@link Color} will be
     * provided.
     *
     * @param legendOptionsParam the {@link GetLegendGraphicRequest} from which to extract label color information.
     * @return the Label {@link Font} {@link Color} extracted from the provided {@link
     *     GetLegendGraphicRequest} or a default {@link Font} {@link Color}.
     */
    public static Color getLabelFontColor(final Map<String, Object> legendOptionsParam) {
        ensureNotNull( legendOptionsParam, "GetLegendGraphicRequestre");
        final Map<String, Object> legendOptions =  legendOptionsParam;
        final String color = legendOptions != null ? (String) legendOptions.get("fontColor") : null;
        if (color == null) {
            // return the default
            return DEFAULT_FONT_COLOR;
        }

        try {
            return color(color);
        } catch (NumberFormatException e) {
            if (LOGGER.isLoggable(Level.WARNING))
                LOGGER.warning(
                        "Could not decode label color: "
                                + color
                                + ", default to "
                                + DEFAULT_FONT_COLOR.toString());
            return DEFAULT_FONT_COLOR;
        }
    }

    /**
     * Checks if the label should be word wrapped
     *
     * @param legendOptionsParam the {@link GetLegendGraphicRequest} from which to extract font antialiasing
     *     information.
     * @return true if the wrap is set to on
     */
    public static boolean isWrap(final Map<String, Object> legendOptionsParam) {
        if (legendOptionsParam.get("wrap") instanceof String) {
            String wrapVal = (String) legendOptionsParam.get("wrap");
            if (wrapVal.equalsIgnoreCase("on")
                    || wrapVal.equalsIgnoreCase("true")
                    || wrapVal.equalsIgnoreCase("yes")
                    || wrapVal.equalsIgnoreCase("1")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the wrap limit
     *
     * @param legendOptionsParam the {@link GetLegendGraphicRequest} from which to extract wrap limit.
     * @return the wrap limit or -1 if no wrap limit provided
     */
    public static int getWrapLimit(final Map<String, Object> legendOptionsParam) {
        if (legendOptionsParam.get("wrap_limit") instanceof String) {
            String wrapVal = (String) legendOptionsParam.get("wrap_limit");
            return Integer.parseInt(wrapVal);
        }
        return -1;
    }

    /**
     * Retrieves row width of legend from the provided {@link GetLegendGraphicRequest}.
     *
     * @param legendOptionsParam a {@link GetLegendGraphicRequest} from which we should extract row width
     *     information.
     * @return the row width specified in the provided {@link GetLegendGraphicRequest} or a default
     *     DEFAULT_ROW_WIDTH.
     */
    public static int getRowWidth(final Map<String, Object> legendOptionsParam) {
        ensureNotNull(legendOptionsParam, "GetLegendGraphicRequestre");
        final Map<String, Object> legendOptions = legendOptionsParam;
        int rowwidth = DEFAULT_ROW_WIDTH;
        if (legendOptions != null && legendOptions.get("rowwidth") != null) {
            try {
                rowwidth = Integer.parseInt((String) legendOptions.get("rowwidth"));
            } catch (NumberFormatException e) {
            }
        }
        return rowwidth;
    }

    /**
     * Retrieves column height of legend from the provided {@link GetLegendGraphicRequest}.
     *
     * @param legendOptionsParam a {@link GetLegendGraphicRequest} from which we should extract column height
     *     information.
     * @return the column height specified in the provided {@link GetLegendGraphicRequest} or a
     *     default DEFAULT_COLUMN_HEIGHT.
     */
    public static int getColumnHeight(final Map<String, Object> legendOptionsParam) {
        ensureNotNull(legendOptionsParam, "GetLegendGraphicRequestre");
        final Map<String, Object> legendOptions = legendOptionsParam;
        int columnheight = DEFAULT_COLUMN_HEIGHT;
        if (legendOptions != null && legendOptions.get("columnheight") != null) {
            try {
                columnheight = Integer.parseInt((String) legendOptions.get("columnheight"));
            } catch (NumberFormatException e) {
            }
        }
        return columnheight;
    }

    /**
     * Retrieves columns of legend from the provided {@link GetLegendGraphicRequest}.
     *
     * @param legendOptionsParam a {@link GetLegendGraphicRequest} from which we should extract columns
     *     information.
     * @return the columns specified in the provided {@link GetLegendGraphicRequest} or a default
     *     DEFAULT_COLUMNS.
     */
    public static int getColumns(final Map<String, Object> legendOptionsParam) {
        ensureNotNull(legendOptionsParam, "GetLegendGraphicRequestre");
        final Map<String, Object> legendOptions = legendOptionsParam;
        int columns = DEFAULT_COLUMNS;
        if (legendOptions != null && legendOptions.get("columns") != null) {
            try {
                columns = Integer.parseInt((String) legendOptions.get("columns"));
            } catch (NumberFormatException e) {
            }
        }
        return columns;
    }

    /**
     * Retrieves rows of legend from the provided {@link GetLegendGraphicRequest}.
     *
     * @param legendOptionsParam a {@link GetLegendGraphicRequest} from which we should extract rows information.
     * @return the rows specified in the provided {@link GetLegendGraphicRequest} or a default
     *     DEFAULT_ROWS.
     */
    public static int getRows(final Map<String, Object> legendOptionsParam) {
        ensureNotNull(legendOptionsParam, "GetLegendGraphicRequestre");
        final Map<String, Object> legendOptions = legendOptionsParam;
        int rows = DEFAULT_ROWS;
        if (legendOptions != null && legendOptions.get("rows") != null) {
            try {
                rows = Integer.parseInt((String) legendOptions.get("rows"));
            } catch (NumberFormatException e) {
            }
        }
        return rows;
    }

    /**
     * Retrieves the legend layout from the provided {@link GetLegendGraphicRequest}.
     *
     * @param legendOptionsParam a {@link GetLegendGraphicRequest} from which we should extract the {@link
     *     LegendLayout} information.
     * @return the {@link LegendLayout} specified in the provided {@link GetLegendGraphicRequest} or
     *     a default DEFAULT_LAYOUT.
     */
    public static LegendLayout getLayout(final Map<String, Object> legendOptionsParam) {
        ensureNotNull(legendOptionsParam, "GetLegendGraphicRequestre");
        final Map<String, Object> legendOptions = legendOptionsParam;
        LegendLayout layout = DEFAULT_LAYOUT;
        if (legendOptions != null && legendOptions.get("layout") != null) {
            try {
                layout = LegendLayout.valueOf(((String) legendOptions.get("layout")).toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        return layout;
    }

    /**
     * Return a {@link BufferedImage} representing this label. The characters '\n' '\r' and '\f' are
     * interpreted as line breaks, as is the character combination "\n" (as opposed to the actual
     * '\n' character). This allows people to force line breaks in their labels by including the
     * character "\" followed by "n" in their label.
     *
     * @param label - the label to render
     * @param g - the Graphics2D that will be used to render this label
     * @return a {@link BufferedImage} of the properly rendered label.
     */
    public static BufferedImage renderLabel(
            String label, final Graphics2D g, final Map<String, Object> legendOptionsParam, int widthParam) {

        ensureNotNull(label);
        ensureNotNull(g);
        ensureNotNull(legendOptionsParam);
        // We'll accept '/n' as a text string
        // to indicate a line break, as well as a traditional 'real' line-break in the XML.
        BufferedImage renderedLabel;
        Color labelColor = getLabelFontColor(legendOptionsParam);
        if (LegendUtils.isWrap(legendOptionsParam)) {
            int width = getWrapLimit(legendOptionsParam);
            if (width == -1) {
                width = widthParam;
            }
            FontMetrics fm = g.getFontMetrics();
            int widthChars = width / fm.stringWidth("m");
            label = WordUtils.wrap(label, widthChars, "\n", true);
        }
        if ((label.indexOf("\n") != -1) || (label.indexOf("\\n") != -1)) {
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
            int y = 0 - g.getFontMetrics().getDescent();
            int c = 0;

            while (st.hasMoreElements()) {
                y += lineHeight.get(c++).intValue();
                rlg.drawString(st.nextToken(), 0, y);
            }
            rlg.dispose();
        } else {
            // this is a traditional 'regular-old' label.  Just figure the
            // size and act accordingly.
            int height = (int) Math.ceil(g.getFontMetrics().getStringBounds(label, g).getHeight());
            int width = (int) Math.ceil(g.getFontMetrics().getStringBounds(label, g).getWidth());
            renderedLabel = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
            rlg.drawString(label, 0, height - rlg.getFontMetrics().getDescent());
            rlg.dispose();
        }

        return renderedLabel;
    }

    /**
     * Retrieves the font from the provided {@link GetLegendGraphicRequest}.
     *
     * @param legendOptionsParam a {@link GetLegendGraphicRequest} from which we should extract the {@link Font}
     *     information.
     * @return the {@link Font} specified in the provided {@link GetLegendGraphicRequest} or a
     *     default {@link Font}.
     */
    public static Font getLabelFont(final Map<String, Object> legendOptionsParam) {
        ensureNotNull(legendOptionsParam, "GetLegendGraphicRequestre");
        final Map<String, Object> legendOptions = legendOptionsParam;
        if (legendOptions == null) return DEFAULT_FONT;
        String legendFontName = LegendUtils.DEFAULT_FONT_NAME;
        if (legendOptions.get("fontName") != null) {
            legendFontName = (String) legendOptions.get("fontName");
        }

        int legendFontFamily = LegendUtils.DEFAULT_FONT_TYPE;
        if (legendOptions.get("fontStyle") != null) {
            String legendFontFamily_ = (String) legendOptions.get("fontStyle");
            if (legendFontFamily_.equalsIgnoreCase("italic")) {
                legendFontFamily = Font.ITALIC;
            } else if (legendFontFamily_.equalsIgnoreCase("bold")) {
                legendFontFamily = Font.BOLD;
            }
        }

        int legendFontSize = LegendUtils.DEFAULT_FONT_SIZE;
        if (legendOptions.get("fontSize") != null) {
            try {
                legendFontSize = Integer.valueOf((String) legendOptions.get("fontSize"));
            } catch (NumberFormatException e) {
                LOGGER.warning(
                        "Error trying to interpret legendOption 'fontSize': "
                                + legendOptions.get("fontSize"));
                legendFontSize = LegendUtils.DEFAULT_FONT_SIZE;
            }
        }

        double dpi = RendererUtilities.getDpi(legendOptionsParam);
        double standardDpi = RendererUtilities.getDpi(Collections.emptyMap());
        if (dpi != standardDpi) {
            double scaleFactor = dpi / standardDpi;
            legendFontSize = (int) Math.ceil(legendFontSize * scaleFactor);
        }

        if (legendFontFamily == LegendUtils.DEFAULT_FONT_TYPE
                && legendFontName.equalsIgnoreCase(LegendUtils.DEFAULT_FONT_NAME)
                && (legendFontSize == LegendUtils.DEFAULT_FONT_SIZE || legendFontSize <= 0))
            return DEFAULT_FONT;

        return new Font(legendFontName, legendFontFamily, legendFontSize);
    }

}
