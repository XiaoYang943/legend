package org.legend.legendGraphicDetails;

import java.util.*;

public class GetLegendGraphicRequest {

    /**
     * holds the geoserver-specific getLegendGraphic options for controlling things like the label
     * font, label font style, label font antialiasing, etc.
     */
    private final Map<String, Object> legendOptions;

    public GetLegendGraphicRequest(Map<String, Object> legendOptions) {
        super();
        this.legendOptions = legendOptions;
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
        return legendOptions == null ? Collections.emptyMap() : legendOptions;
    }

}
