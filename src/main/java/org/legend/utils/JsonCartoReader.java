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
import org.legend.model.TextComponent;

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
    MapDocument frame;

    /**
     * Read a json file describing map and decoration parameters, and build a map and its decorations.
     * @param jsonFilePath the path of the json file
     */
    public void readAndBuildMap(String jsonFilePath) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<List<Integer>>() {
        });

        JsonNode mapDocumentArrayNode = mapper.readTree(new File(jsonFilePath)).get("mapDocument");
        this.frame = new MapDocument();
        JsonNode mapArrayNode = mapper.readTree(new File(jsonFilePath)).get("map");
        int mapBufferedImageMaxWidth = 0;
        int mapBufferedImageMaxHeight = 0;
        // Look for the minimal size (so the size of the biggest map) to build the mapDocument
        for (JsonNode jsonNode : mapArrayNode) {
            if(jsonNode.get("layers").toString().replace("\"", "").contains("[")) {
                MapContent mapContent = new MapContent();
                ObjectReader listReader = mapper.readerFor(new TypeReference<List<String>>() {
                });
                List<String> layerList = listReader.readValue(jsonNode.get("layers"));
                List<String> sldList = listReader.readValue(jsonNode.get("sld"));
                for(int i = 0; i < layerList.size(); i++){
                    FeatureLayer layer = LayerUtils.buildLayer(layerList.get(i), sldList.get(i));
                    mapContent.addLayer(layer);
                }
                MapComponent modelMap = new MapComponent(mapContent);
                BufferedImage mapBufferedImage = modelMap.paintMap(Integer.parseInt(jsonNode.get("size").toString().replace("\"", "")), Boolean.parseBoolean(jsonNode.get("frame").toString()),Integer.parseInt(jsonNode.get("frameRightExtension").toString().replace("\"", "")));
                mapBufferedImageMaxWidth = java.lang.Math.max(mapBufferedImage.getWidth(), mapBufferedImageMaxWidth);
                mapBufferedImageMaxHeight = java.lang.Math.max(mapBufferedImage.getHeight(), mapBufferedImageMaxHeight);
                mapContent.dispose();
            } else {
                FeatureLayer layer = LayerUtils.buildLayer(jsonNode.get("layers").toString().replace("\"", ""), jsonNode.get("sld").toString().replace("\"", ""));
                MapContent mapContent = new MapContent();
                mapContent.addLayer(layer);
                MapComponent modelMap = new MapComponent(mapContent);
                BufferedImage mapBufferedImage = modelMap.paintMap(Integer.parseInt(jsonNode.get("size").toString().replace("\"", "")), Boolean.parseBoolean(jsonNode.get("frame").toString()),Integer.parseInt(jsonNode.get("frameRightExtension").toString().replace("\"", "")));
                mapBufferedImageMaxWidth = java.lang.Math.max(mapBufferedImage.getWidth(), mapBufferedImageMaxWidth);
                mapBufferedImageMaxHeight = java.lang.Math.max(mapBufferedImage.getHeight(), mapBufferedImageMaxHeight);
                mapContent.dispose();
            }
        }
        frame.setSize(mapBufferedImageMaxWidth, mapBufferedImageMaxHeight, Integer.parseInt(mapDocumentArrayNode.findValue("margin").toString().replace("\"", "")));
        frame.setBufferedImage(mapDocumentArrayNode.findValue("landscape").toString().replace("\"", ""), Integer.parseInt(mapDocumentArrayNode.findValue("extraHeight").toString().replace("\"", "")), Integer.parseInt(mapDocumentArrayNode.findValue("extraWidth").toString().replace("\"", "")));

        MapContent mapContent2 = new MapContent();
        for (JsonNode jsonNode : mapArrayNode) {
            if(jsonNode.get("layers").toString().replace("\"", "").contains("[")) {
                ObjectReader listReader = mapper.readerFor(new TypeReference<List<String>>() {
                });
                List<String> layerList = listReader.readValue(jsonNode.get("layers"));
                List<String> sldList = listReader.readValue(jsonNode.get("sld"));
                for(int i = 0; i < layerList.size(); i++){
                    FeatureLayer layer = LayerUtils.buildLayer(layerList.get(i), sldList.get(i));
                    mapContent2.addLayer(layer);
                }
                MapComponent modelMap = new MapComponent(mapContent2);
                BufferedImage mapBufferedImage = modelMap.paintMap(1000, true,Integer.parseInt(jsonNode.get("frameRightExtension").toString().replace("\"", "")));
                if (jsonNode.get("position").isArray()) {
                    this.g = frame.paintMapOnMapDocument(reader.readValue(jsonNode.get("position")), mapBufferedImage, g, getColor(mapDocumentArrayNode.findValue("bgColor").toString().replace("\"", "")));
                } else {
                    this.g = frame.paintMap(jsonNode.findValue("position").toString().replace("[", "").replace("]", "").replace("\"", ""), mapBufferedImage, g, getColor(mapDocumentArrayNode.findValue("bgColor").toString().replace("\"", "")));
                }
                mapContent2.dispose();
            } else {
                FeatureLayer layer = LayerUtils.buildLayer(jsonNode.get("layers").toString().replace("\"", ""), jsonNode.get("sld").toString().replace("\"", ""));
                MapContent mapContent3 = new MapContent();
                mapContent3.addLayer(layer);
                MapComponent modelMap = new MapComponent(mapContent3);
                BufferedImage mapBufferedImage = modelMap.paintMap(Integer.parseInt(jsonNode.get("size").toString().replace("\"", "")), Boolean.parseBoolean(jsonNode.get("frame").toString()),Integer.parseInt(jsonNode.get("frameRightExtension").toString().replace("\"", "")));
                if (jsonNode.get("position").isArray()) {
                    this.g = frame.paintMapOnMapDocument(reader.readValue(jsonNode.get("position")), mapBufferedImage, g, getColor(mapDocumentArrayNode.findValue("bgColor").toString().replace("\"", "")));
                } else {
                    this.g = frame.paintMap(jsonNode.findValue("position").toString().replace("[", "").replace("]", "").replace("\"", ""), mapBufferedImage, g, getColor(mapDocumentArrayNode.findValue("bgColor").toString().replace("\"", "")));
                }
                mapContent3.dispose();
            }
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
            legendOptions.put("labelXposition", Integer.parseInt(jsonNode.get("labelXposition").toString().replace("\"","")));
            legendOptions.put("labelXOffset", Integer.parseInt(jsonNode.get("labelXOffset").toString().replace("\"","")));
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
        if(textArrayNode != null) {
            for (JsonNode jsonNode : textArrayNode) {
                Font titleFont = new Font(jsonNode.get("fontName").toString().replace("\"", ""), getFontStyle(jsonNode.get("fontStyle").toString().replace("\"", "")), Integer.parseInt(jsonNode.get("fontSize").toString()));
                String color1 = jsonNode.get("fontColor").toString().replace("\"", "");
                Field field = Class.forName("java.awt.Color").getField(color1);
                Color color = (Color) field.get(null);
                TextComponent textComponent = new TextComponent(jsonNode.get("content").toString().replace("\"", ""), color, titleFont, Boolean.parseBoolean(jsonNode.get("underlined").toString()));
                BufferedImage titleBufferedImage = textComponent.paintText(Boolean.parseBoolean(jsonNode.get("vertical").toString()));
                if (jsonNode.get("position").isArray()) {
                    textComponent.setPosition(reader.readValue(jsonNode.get("position")));
                } else {
                    textComponent.setPosition(jsonNode.get("position").toString().replace("[", "").replace("]", "").replace("\"", ""), frame.getImgWidth(), frame.getImgHeight(), titleBufferedImage);
                }
                g.drawImage(titleBufferedImage, textComponent.getPositionX(), textComponent.getPositionY(), null);
            }
        }

        JsonNode imageArrayNode = mapper.readTree(new File(jsonFilePath)).get("image");
        if(imageArrayNode != null) {
            for (JsonNode jsonNode : imageArrayNode) {
                Compass compass = new Compass(jsonNode.get("svg").toString().replace("\"", ""));
                BufferedImage compassBufIma = compass.paintCompass(Integer.parseInt(jsonNode.get("size").toString().replace("\"", "")));
                double angle = compass.getRotationToNorth(mapContent2, frame.getImgWidth(), frame.getImgHeight());
                BufferedImage rotatedCompassBufIma = Compass.rotate(compassBufIma, angle);
                if (jsonNode.get("position").isArray()) {
                    compass.setPosition(reader.readValue(jsonNode.get("position")));
                } else {
                    compass.setPosition(jsonNode.get("position").toString().replace("\"", ""), frame.getImgWidth(), frame.getImgHeight(), rotatedCompassBufIma);
                }
                g.drawImage(rotatedCompassBufIma, compass.getPositionX(), compass.getPositionY(), null);
            }
        }

        JsonNode scaleArrayNode = mapper.readTree(new File(jsonFilePath)).get("scale");
        if(scaleArrayNode != null) {
            for (JsonNode jsonNode : scaleArrayNode) {
                FeatureLayer layer = LayerUtils.buildLayer(jsonNode.get("layers").toString().replace("\"", ""), jsonNode.get("sld").toString().replace("\"", ""));
                ScaleComponent mapScaleComponent = new ScaleComponent(layer, frame.getImgWidth());
                mapScaleComponent.setStrokeWidth(Integer.parseInt(jsonNode.get("strokeWidth").toString().replace("\"", "")));
                mapScaleComponent.setFont(new Font(jsonNode.get("fontName").toString().replace("\"", ""), getFontStyle(jsonNode.get("fontStyle").toString().replace("\"", "")), Integer.parseInt(jsonNode.get("fontSize").toString().replace("\"", ""))));
                BufferedImage scaleBufferedImage = mapScaleComponent.paintMapScale(jsonNode.get("bars").toString().replace("\"", ""));
                if (jsonNode.get("position").isArray()) {
                    mapScaleComponent.setPosition(reader.readValue(jsonNode.get("position")));
                } else {
                    mapScaleComponent.setPosition(jsonNode.get("position").toString().replace("\"", ""), frame.getImgWidth(), frame.getImgHeight(), scaleBufferedImage);
                }
                g.drawImage(scaleBufferedImage, mapScaleComponent.getPositionX(), mapScaleComponent.getPositionY(), null);
            }
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
