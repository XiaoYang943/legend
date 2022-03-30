package org.legend.legendGraphicDetails;

import org.geotools.feature.NameImpl;
import org.geotools.styling.Style;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

public class LegendRequest {

    private String layer;
    private Name layerName;
    private FeatureType featureType;
    private String styleName;
    private String title;

    /** Optional rule used to refine presentation of style */
    private String rule;

    /** Style determined from a review of request parameters */
    private Style style;

    /** LegendRequest for a style, no associated featureType. */
    public LegendRequest() {
        this.layer = "";
        this.featureType = null;
        this.layerName = new NameImpl("");
    }

}
