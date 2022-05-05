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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.legend.imageBuilder.BufferedImageLegendGraphicBuilder;
import org.legend.model.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

/**
 * Read a JSON map file and build a map
 * @author Adrien Bessy
 */
public class JsonCartoReader {

    BufferedImage mapBufferedImage;
    FeatureLayer layer;
    Graphics2D g;
    BaseFrame frame;
    MapContent mapContent;

    /**
     * Read a json file describing map and decoration parameters, and build a map and its decorations.
     * @param jsonFilePath the path of the json file
     */
    public void readAndBuildMap(String jsonFilePath) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode mapArrayNode = mapper.readTree(new File(jsonFilePath)).get("map");
        this.layer = LayerUtils.buildLayer(mapArrayNode.findValue("layers").toString().replace("\"",""), mapArrayNode.findValue("sld").toString().replace("\"",""));
        this.mapContent = new MapContent();
        mapContent.addLayer(layer);
        org.legend.model.MapItem modelMap = new org.legend.model.MapItem(mapContent);
        this.mapBufferedImage = modelMap.paintMap();
        this.frame = new BaseFrame();
        frame.setBaseFrameSize(mapBufferedImage, Integer.parseInt(mapArrayNode.findValue("margin").toString().replace("\"","")));
        frame.setBufferedImage(mapArrayNode.findValue("landscape").toString().replace("\"",""));
        this.g = frame.paintMapOnBaseFrame(mapArrayNode.findValue("position").toString().replace("\"",""));

        JsonNode legendArrayNode = mapper.readTree(new File(jsonFilePath)).get("legend");
        for (JsonNode jsonNode : legendArrayNode) {
            List<FeatureLayer> layerList = new ArrayList<>();
            layerList.add(layer);
            Map legendOptions = new HashMap<>();
            legendOptions.put("transparent", jsonNode.get("transparent").toString().replace("\"","")); // default is off
            legendOptions.put("bgColor", jsonNode.get("bgColor").toString().replace("\"","")); // default is Color.WHITE;
            legendOptions.put("ruleLabelMargin", Integer.parseInt(jsonNode.get("ruleLabelMargin").toString().replace("\"",""))); //default is 3;
            legendOptions.put("verticalRuleMargin", Integer.parseInt(jsonNode.get("verticalRuleMargin").toString().replace("\"",""))); //default is 0;
            legendOptions.put("horizontalRuleMargin", Integer.parseInt(jsonNode.get("horizontalRuleMargin").toString().replace("\"",""))); //default is 0;
            legendOptions.put("verticalMarginBetweenLayers", Integer.parseInt(jsonNode.get("verticalMarginBetweenLayers").toString().replace("\"",""))); //default is 0;
            legendOptions.put("horizontalMarginBetweenLayers", Integer.parseInt(jsonNode.get("horizontalMarginBetweenLayers").toString().replace("\"",""))); //default is 0;
            legendOptions.put("fontName", jsonNode.get("fontName").toString().replace("\"","")); //default is "Sans-Serif"
            legendOptions.put("fontStyle", jsonNode.get("fontStyle").toString().replace("\"",""));
            Field field = Class.forName("java.awt.Color").getField(jsonNode.get("fontColor").toString().replace("\"",""));
            Color color = (Color) field.get(null);
            legendOptions.put("fontColor", color); // default is Color.BLACK;
            legendOptions.put("fontSize", jsonNode.get("fontSize").toString().replace("\"","")); // default is 12;
            BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();
            BufferedImage legendBufferedImage = builder.buildLegendGraphic(layerList, legendOptions);
            Legend modelLegend = new Legend();
            modelLegend.setPosition(jsonNode.get("position").toString().replace("\"",""), frame.getImgWidth(), frame.getImgHeight(), legendBufferedImage);
            g.drawImage(legendBufferedImage, modelLegend.getPositionX(), modelLegend.getPositionY(), null);
        }

        JsonNode textArrayNode = mapper.readTree(new File(jsonFilePath)).get("text");
        for (JsonNode jsonNode : textArrayNode) {
            Font titleFont = new Font(jsonNode.get("fontName").toString().replace("\"",""), setFontStyle(jsonNode.get("fontStyle").toString().replace("\"","")), Integer.parseInt(jsonNode.get("fontSize").toString()));
            String color1 = jsonNode.get("fontColor").toString().replace("\"","");
            Field field = Class.forName("java.awt.Color").getField(color1);
            Color color = (Color) field.get(null);
            TextItem textItem = new TextItem(jsonNode.get("content").toString().replace("\"",""), color, titleFont, Boolean.parseBoolean(jsonNode.get("underlined").toString()));
            BufferedImage titleBufferedImage = textItem.paintText();
            String jsonPosition = jsonNode.get("position").toString().replace("[","").replace("]","").replace("\"","");
            textItem.setPosition(jsonPosition, frame.getImgWidth(), frame.getImgHeight(), titleBufferedImage);
            g.drawImage(titleBufferedImage, textItem.getPositionX(), textItem.getPositionY(), null);
        }

        JsonNode compassArrayNode = mapper.readTree(new File(jsonFilePath)).get("compass");
        for (JsonNode jsonNode : compassArrayNode) {
            Compass compass = new Compass(jsonNode.get("svg").toString().replace("\"",""));
            BufferedImage compassBufIma = compass.paintCompass(Integer.parseInt(jsonNode.get("size").toString().replace("\"","")));
            compass.setPosition(jsonNode.get("position").toString().replace("\"",""), frame.getImgWidth(), frame.getImgHeight(), compassBufIma);
            g.drawImage(compassBufIma, compass.getPositionX(), compass.getPositionY(), null);
        }

        JsonNode scaleArrayNode = mapper.readTree(new File(jsonFilePath)).get("scale");
        for (JsonNode jsonNode : scaleArrayNode) {
            Scale mapScale = new Scale(mapContent, frame.getImgWidth());
            mapScale.setStrokeWidth(Integer.parseInt(jsonNode.get("strokeWidth").toString().replace("\"","")));
            mapScale.setFont(new Font(jsonNode.get("fontName").toString().replace("\"",""), setFontStyle(jsonNode.get("fontStyle").toString().replace("\"","")), Integer.parseInt(jsonNode.get("fontSize").toString().replace("\"",""))));
            BufferedImage scaleBufferedImage = mapScale.paintMapScale(jsonNode.get("bars").toString().replace("\"",""));
            mapScale.setPosition(jsonNode.get("position").toString().replace("\"",""), frame.getImgWidth(), frame.getImgHeight(), scaleBufferedImage);
            g.drawImage(scaleBufferedImage, mapScale.getPositionX(), mapScale.getPositionY(), null);
        }
        g.dispose();
        JsonNode outputArrayNode = mapper.readTree(new File(jsonFilePath)).get("output");
        String resultFileName = outputArrayNode.findValue("fileName").toString().replace("\"","") + "." + outputArrayNode.findValue("fileType").toString().replace("\"","");
        ImageIO.write(frame.getBaseFrameBufferedImage(), outputArrayNode.findValue("fileType").toString().replace("\"","").toUpperCase(), new File(outputArrayNode.findValue("filePath").toString().replace("\"","") + resultFileName));
    }

    private int setFontStyle(String fontStyleString){
        int style = 0;
        switch (fontStyleString) {
            case "bold":
                style = Font.BOLD;
                break;
            case "italic":
                style = Font.ITALIC;
                break;
        }
        return style;
    }

}
