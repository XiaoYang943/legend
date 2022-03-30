package org.legend.legendGraphicDetails;

import java.util.*;

public class GetLegendGraphicRequest {

    /** Legend option to enable feature count matching */
    public static final String COUNT_MATCHED_KEY = "countMatched";

    /** Legend option to enable feature count matching */
    public static final String HIDE_EMPTY_RULES = "hideEmptyRules";

    public static final String SLD_VERSION = "1.0.0";

    /** default legend graphic width, in pixels, to apply if no WIDTH parameter was passed */
    public static final int DEFAULT_WIDTH = 20;

    /** default legend graphic height, in pixels, to apply if no WIDTH parameter was passed */
    public static final int DEFAULT_HEIGHT = 20;

    /**
     * The default image format in which to produce a legend graphic. Not really used when
     * performing user requests, since FORMAT is a mandatory parameter, but by now serves as a
     * default for expressing LegendURL layer attribute in GetCapabilities.
     */
    public static final String DEFAULT_FORMAT = "image/png";

    /** The featuretype(s) of the requested LAYER(s) */
    private List<LegendRequest> legends = new ArrayList<>();

    /**
     * should hold FEATURETYPE parameter value, though not used by now, since GeoServer WMS still
     * does not supports nested layers and layers has only a single feature type. This should change
     * in the future.
     */
    private String featureType;

    /**
     * holds the standarized scale denominator passed as the SCALE parameter value, or <code>-1.0
     * </code> if not provided
     */
    private double scale = -1d;

    /**
     * the mime type of the file format in which to return the legend graphic, as requested by the
     * FORMAT request parameter value.
     */
    private String format;

    /**
     * the width in pixels of the returned graphic, or <code>DEFAULT_WIDTH</code> if not provided
     */
    private int width = DEFAULT_WIDTH;

    /**
     * the height in pixels of the returned graphic, or <code>DEFAULT_HEIGHT</code> if not provided
     */
    private int height = DEFAULT_HEIGHT;

    /**
     * holds the geoserver-specific getLegendGraphic options for controlling things like the label
     * font, label font style, label font antialiasing, etc.
     */
    private Map<String, Object> legendOptions;

    /** Whether the legend graphic background shall be transparent or not. */
    private boolean transparent;

    private boolean strict = true;

    /** Optional locale to be used for text in output. */
    private Locale locale;

    public GetLegendGraphicRequest(int width, int height) {
        super();
        this.width=width;
        this.height=height;
    }

    /**
     * Returns the possibly empty set of key/value pair parameters to control some aspects of legend
     * generation.
     *
     * <p>These parameters are meant to be passed as the request parameter <code>"LEGEND_OPTIONS"
     * </code> with the format <code>LEGEND_OPTIONS=multiKey:val1,val2,val3;singleKey:val</code>.
     *
     * <p>The known options, all optional, are:
     *
     * <ul>
     *   <li><code>fontName</code>: name of the system font used for legend rule names. Defaults to
     *       "Sans-Serif"
     *   <li><code>fontStyle</code>: one of "plain", "italic" or "bold"
     *   <li><code>fontSize</code>: integer for the font size in pixels
     *   <li><code>fontColor</code>: a <code>String</code> that represents an opaque color as a
     *       24-bit integer
     *   <li><code>bgColor</code>: allows to override the legend background color
     *   <li><code>fontAntiAliasing</code>: a boolean indicating whether to use antia aliasing in
     *       font rendering. Anything of the following works: "yes", "true", "1". Anything else
     *       means false.
     *   <li><code>forceLabels</code>: "on" means labels will always be drawn, even if only one rule
     *       is available. "off" means labels will never be drawn, even if multiple rules are
     *       available.
     *   <li><code>forceTitles</code>: "off" means titles will never be drawn, even if multiple
     *       layers are available.
     *   <li><code>minSymbolSize</code>: a number defining the minimum size to be rendered for a
     *       symbol (defaults to 3).
     * </ul>
     *
     * @return Map<String,Object>
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getLegendOptions() {
        return (Map<String, Object>)
                (legendOptions == null ? Collections.emptyMap() : legendOptions);
    }

}
