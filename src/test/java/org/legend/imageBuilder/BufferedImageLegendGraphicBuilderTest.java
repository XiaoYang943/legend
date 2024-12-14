package org.legend.imageBuilder;

import junit.framework.TestCase;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.*;
import org.geotools.api.style.Stroke;
import org.geotools.data.geojson.store.GeoJSONDataStoreFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.util.URLs;
import org.geotools.xml.styling.SLDParser;
import org.legend.utils.LayerUtils;
import org.locationtech.jts.geom.Geometry;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BufferedImageLegendGraphicBuilderTest extends TestCase {


    public List<FeatureLayer> produceLayerList() throws Exception {
         File file2 = new File("D:\\data\\vector\\shp\\国土资源shp\\地类图斑_安康市.shp");
        Map<String, String> connect2 = new HashMap<>();
        connect2.put("url", file2.toURI().toString());
        DataStore dataStore2 = DataStoreFinder.getDataStore(connect2);
        String[] typeNames2 = dataStore2.getTypeNames();
        String typeName2 = typeNames2[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource2 = dataStore2.getFeatureSource(typeName2);
        Style sld4 = getSldStyle("C:\\Users\\admin\\Desktop\\安康1.sld");
        FeatureLayer layer4 = new FeatureLayer(featureSource2, sld4);

        List<FeatureLayer> layerList = new ArrayList<>();
        layerList.add(layer4);
        return layerList;
    }




    /**
     * Extract the style from sld file.
     *
     * @param sldFilePath the sld file path
     * @return the style
     */
    private Style getSldStyle(String sldFilePath) throws IOException {
        StyleFactory styleFactory3 = CommonFactoryFinder.getStyleFactory();
        FeatureTypeStyle featureTypeStyle3 = styleFactory3.createFeatureTypeStyle();

        Path path = Paths.get(sldFilePath);
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("SvgParameter", "CssParameter");
        content = content.replaceAll("sld", "se");
        Files.write(path, content.getBytes(charset));

        SLDParser styleReader = null;
        try {
            styleReader = new SLDParser(styleFactory3, new File(sldFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert styleReader != null;
        Style sld = styleReader.readXML()[0];
        sld.featureTypeStyles().add(featureTypeStyle3);
        return sld;
    }

    /**
     *
     */
    public void testBuildLegendGraphic() throws Exception {
        Map legendOptions =new HashMap<>();
        legendOptions.put("width",35); // default is 50
        legendOptions.put("height",35); // default is 50
        //legendOptions.put("forceRuleLabelsOff","on");
        legendOptions.put("transparent","off"); // default is off
        legendOptions.put("bgColor",Color.WHITE); // default is Color.WHITE;    // 背景色
        // Set the space between the image and the rule label
        legendOptions.put("ruleLabelMargin",0); //default is 3;    // 没用
        legendOptions.put("verticalRuleMargin",0); //default is 0; // rule垂直margin
        legendOptions.put("horizontalRuleMargin",20); //default is 0;// rule水平margin
        legendOptions.put("layout","VERTICAL"); //default is VERTICAL;(HORIZONTAL\VERTICAL)  // 布局方向
        legendOptions.put("verticalMarginBetweenLayers", 0); //default is 0;    // 没用
        legendOptions.put("horizontalMarginBetweenLayers", 0); //default is 0;  // 没用
        legendOptions.put("fontName", "TimesRoman"); //default is "Sans-Serif"
        legendOptions.put("fontStyle", "bold");
        legendOptions.put("fontColor",Color.BLACK); // default is Color.BLACK;  // 字体颜色
        legendOptions.put("fontSize","14"); // default is 12;   // 字体大小
        legendOptions.put("labelXposition",0);  // 标题-margin-left
        legendOptions.put("labelXOffset",-50);  // rule文字-margin-left

        legendOptions.put("test",50);  // rule文字-margin-left
        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();
        BufferedImage bufferedImage = builder.buildLegendGraphic(produceLayerList(),legendOptions);
        
        int padding = 100;
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth()
                + padding *2, bufferedImage.getHeight() + padding *2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(bufferedImage, padding, padding, null);
        g.dispose();
        ImageIO.write(bufferedImage,"png",new FileOutputStream("data/legend/output/legend4.png"));
    }
}