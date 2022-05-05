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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
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

    Graphics2D g;
    BaseFrame frame;

    /**
     * Read a json file describing map and decoration parameters, and build a map and its decorations.
     * @param jsonFilePath the path of the json file
     */
    public void readAndBuildMap(String jsonFilePath) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<List<Integer>>() {
        });

        JsonNode mapDocumentArrayNode = mapper.readTree(new File(jsonFilePath)).get("mapDocument");
        this.frame = new BaseFrame();
        JsonNode mapArrayNode = mapper.readTree(new File(jsonFilePath)).get("map");
        int mapBufferedImageMaxWidth = 0;
        int mapBufferedImageMaxHeight = 0;
        for (JsonNode jsonNode : mapArrayNode) {
            FeatureLayer layer = LayerUtils.buildLayer(jsonNode.get("layers").toString().replace("\"", ""), jsonNode.get("sld").toString().replace("\"", ""));
            MapContent mapContent = new MapContent();
            mapContent.addLayer(layer);
            org.legend.model.MapItem modelMap = new org.legend.model.MapItem(mapContent);
            BufferedImage mapBufferedImage = modelMap.paintMap(Integer.parseInt(jsonNode.get("size").toString().replace("\"", "")));
            mapBufferedImageMaxWidth = java.lang.Math.max(mapBufferedImage.getWidth(), mapBufferedImageMaxWidth);
            mapBufferedImageMaxHeight = java.lang.Math.max(mapBufferedImage.getHeight(), mapBufferedImageMaxHeight);
            mapContent.dispose();
        }
        frame.setBaseFrameSize(mapBufferedImageMaxWidth, mapBufferedImageMaxHeight, Integer.parseInt(mapDocumentArrayNode.findValue("margin").toString().replace("\"", "")));
        frame.setBufferedImage(mapDocumentArrayNode.findValue("landscape").toString().replace("\"", ""));

        for (JsonNode jsonNode : mapArrayNode) {
            FeatureLayer layer = LayerUtils.buildLayer(jsonNode.get("layers").toString().replace("\"", ""), jsonNode.get("sld").toString().replace("\"", ""));
            MapContent mapContent = new MapContent();
            mapContent.addLayer(layer);
            org.legend.model.MapItem modelMap = new org.legend.model.MapItem(mapContent);
            BufferedImage mapBufferedImage = modelMap.paintMap(Integer.parseInt(jsonNode.get("size").toString().replace("\"", "")));
            if(jsonNode.get("position").isArray()){
                this.g = frame.paintMapOnMapDocument(reader.readValue(jsonNode.get("position")), mapBufferedImage, g);
            } else{
                this.g = frame.paintMapOnBaseFrame(jsonNode.findValue("position").toString().replace("[","").replace("]","").replace("\"", ""), mapBufferedImage, g);
            }
            mapContent.dispose();
        }

        JsonNode legendArrayNode = mapper.readTree(new File(jsonFilePath)).get("legend");
        for (JsonNode jsonNode : legendArrayNode) {
            List<FeatureLayer> layerList = new ArrayList<>();
            FeatureLayer layer = LayerUtils.buildLayer(jsonNode.get("layers").toString().replace("\"", ""), jsonNode.get("sld").toString().replace("\"", ""));
            layerList.add(layer);
            Map legendOptions = new HashMap<>();
            legendOptions.put("transparent", jsonNode.get("transparent").toString().replace("\"","")); // default is off
            legendOptions.put("bgColor", getColor(jsonNode.get("bgColor").toString().replace("\"",""))); // default is Color.WHITE;
            legendOptions.put("ruleLabelMargin", Integer.parseInt(jsonNode.get("ruleLabelMargin").toString().replace("\"",""))); //default is 3;
            legendOptions.put("verticalRuleMargin", Integer.parseInt(jsonNode.get("verticalRuleMargin").toString().replace("\"",""))); //default is 0;
            legendOptions.put("horizontalRuleMargin", Integer.parseInt(jsonNode.get("horizontalRuleMargin").toString().replace("\"",""))); //default is 0;
            legendOptions.put("layout", jsonNode.get("layout").toString().replace("\"","")); //default is VERTICAL;
            legendOptions.put("verticalMarginBetweenLayers", Integer.parseInt(jsonNode.get("verticalMarginBetweenLayers").toString().replace("\"",""))); //default is 0;
            legendOptions.put("horizontalMarginBetweenLayers", Integer.parseInt(jsonNode.get("horizontalMarginBetweenLayers").toString().replace("\"",""))); //default is 0;
            legendOptions.put("fontName", jsonNode.get("fontName").toString().replace("\"","")); //default is "Sans-Serif"
            legendOptions.put("fontStyle", jsonNode.get("fontStyle").toString().replace("\"",""));
            legendOptions.put("fontColor", getColor(jsonNode.get("fontColor").toString().replace("\"",""))); // default is Color.BLACK;
            legendOptions.put("fontSize", jsonNode.get("fontSize").toString().replace("\"","")); // default is 12;
            BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();
            BufferedImage legendBufferedImage = builder.buildLegendGraphic(layerList, legendOptions);
            Legend modelLegend = new Legend();
            if(jsonNode.get("position").isArray()){
                modelLegend.setPosition(reader.readValue(jsonNode.get("position")));
            }else {
                modelLegend.setPosition(jsonNode.get("position").toString().replace("[", "").replace("]", "").replace("\"", ""), frame.getImgWidth(), frame.getImgHeight(), legendBufferedImage);
            }
            g.drawImage(legendBufferedImage, modelLegend.getPositionX(), modelLegend.getPositionY(), null);
        }

        JsonNode textArrayNode = mapper.readTree(new File(jsonFilePath)).get("text");
        for (JsonNode jsonNode : textArrayNode) {
            Font titleFont = new Font(jsonNode.get("fontName").toString().replace("\"",""), getFontStyle(jsonNode.get("fontStyle").toString().replace("\"","")), Integer.parseInt(jsonNode.get("fontSize").toString()));
            String color1 = jsonNode.get("fontColor").toString().replace("\"","");
            Field field = Class.forName("java.awt.Color").getField(color1);
            Color color = (Color) field.get(null);
            TextItem textItem = new TextItem(jsonNode.get("content").toString().replace("\"",""), color, titleFont, Boolean.parseBoolean(jsonNode.get("underlined").toString()));
            BufferedImage titleBufferedImage = textItem.paintText();
            if(jsonNode.get("position").isArray()){
                textItem.setPosition(reader.readValue(jsonNode.get("position")));
            } else{
                textItem.setPosition(jsonNode.get("position").toString().replace("[","").replace("]","").replace("\"",""), frame.getImgWidth(), frame.getImgHeight(), titleBufferedImage);
            }
            g.drawImage(titleBufferedImage, textItem.getPositionX(), textItem.getPositionY(), null);
        }

        JsonNode compassArrayNode = mapper.readTree(new File(jsonFilePath)).get("compass");
        for (JsonNode jsonNode : compassArrayNode) {
            Compass compass = new Compass(jsonNode.get("svg").toString().replace("\"",""));
            BufferedImage compassBufIma = compass.paintCompass(Integer.parseInt(jsonNode.get("size").toString().replace("\"","")));
            if(jsonNode.get("position").isArray()){
                compass.setPosition(reader.readValue(jsonNode.get("position")));
            } else {
                compass.setPosition(jsonNode.get("position").toString().replace("\"", ""), frame.getImgWidth(), frame.getImgHeight(), compassBufIma);
            }
            g.drawImage(compassBufIma, compass.getPositionX(), compass.getPositionY(), null);
        }

        JsonNode scaleArrayNode = mapper.readTree(new File(jsonFilePath)).get("scale");
        for (JsonNode jsonNode : scaleArrayNode) {
            FeatureLayer layer = LayerUtils.buildLayer(jsonNode.get("layers").toString().replace("\"", ""), mapArrayNode.findValue("sld").toString().replace("\"", ""));
            MapContent mapContent = new MapContent();
            mapContent.addLayer(layer);
            Scale mapScale = new Scale(mapContent, frame.getImgWidth());
            mapScale.setStrokeWidth(Integer.parseInt(jsonNode.get("strokeWidth").toString().replace("\"","")));
            mapScale.setFont(new Font(jsonNode.get("fontName").toString().replace("\"",""), getFontStyle(jsonNode.get("fontStyle").toString().replace("\"","")), Integer.parseInt(jsonNode.get("fontSize").toString().replace("\"",""))));
            BufferedImage scaleBufferedImage = mapScale.paintMapScale(jsonNode.get("bars").toString().replace("\"",""));
            if(jsonNode.get("position").isArray()){
                mapScale.setPosition(reader.readValue(jsonNode.get("position")));
            } else {
                mapScale.setPosition(jsonNode.get("position").toString().replace("\"", ""), frame.getImgWidth(), frame.getImgHeight(), scaleBufferedImage);
            }
            g.drawImage(scaleBufferedImage, mapScale.getPositionX(), mapScale.getPositionY(), null);
            mapContent.dispose();
        }
        g.dispose();
        JsonNode outputArrayNode = mapper.readTree(new File(jsonFilePath)).get("output");
        String resultFileName = outputArrayNode.findValue("fileName").toString().replace("\"","") + "." + outputArrayNode.findValue("fileType").toString().replace("\"","");
        ImageIO.write(frame.getBaseFrameBufferedImage(), outputArrayNode.findValue("fileType").toString().replace("\"","").toUpperCase(), new File(outputArrayNode.findValue("filePath").toString().replace("\"","") + resultFileName));
    }

    private int getFontStyle(String fontStyleString){
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

    private Object getColor(String stringColor) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Object bgColor;
        if(!stringColor.contains("#")) {
            Field bgColorField = Class.forName("java.awt.Color").getField(stringColor);
            bgColor = bgColorField.get(null);
        }else{
            bgColor = stringColor;
        }
        return bgColor;
    }

}
