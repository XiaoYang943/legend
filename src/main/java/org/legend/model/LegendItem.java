package org.legend.model;

import org.geotools.map.FeatureLayer;
import org.legend.imageBuilder.BufferedImageLegendGraphicBuilder;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class LegendItem {

    final private List<FeatureLayer> featureLayerList;

    final private Map<String, Object> legendOptions;

    public LegendItem(List<FeatureLayer> featureLayerList, Map<String, Object> legendOptions){
        this.featureLayerList = featureLayerList;
        this.legendOptions = legendOptions;
    }

    public BufferedImage produceBufferedImage() throws Exception {

        int defaultWidth = 50;
        int defaultHeight = 50;

        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();
        if (!legendOptions.containsKey("width")){
            legendOptions.put("width",defaultWidth);
        }
        if (!legendOptions.containsKey("height")){
            legendOptions.put("height",defaultHeight);
        }
        return builder.buildLegendGraphic(featureLayerList,legendOptions);
    }

}
