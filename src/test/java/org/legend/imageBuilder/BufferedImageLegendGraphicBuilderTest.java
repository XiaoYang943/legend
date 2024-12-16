package org.legend.imageBuilder;

import junit.framework.TestCase;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.xml.styling.SLDParser;
import org.legend.options.LegendOptions;
import org.legend.utils.LegendUtils;

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

        // TODO-hyy
        LegendOptions legendOptionsNew = LegendOptions.builder()
                .width(35)
                .height(35)
                .transparent(false)
                .bgColor("bgColor")
                .ruleLabelMargin(0)    // 没用
                .verticalRuleMargin(0) // rule垂直margin
                .horizontalRuleMargin(20)// rule水平margin
                .layout(LegendUtils.LegendLayout.VERTICAL)  // 布局方向
                .verticalMarginBetweenLayers(0)   // 没用
                .horizontalMarginBetweenLayers(0)   // 没用
                .fontName("TimesRoman")
                .fontStyle("bold")
                .fontColor("fontColor")
                .fontSize(14)
                .labelXposition(0)  // 标题-margin-left
                .labelXOffset(-50)  // rule文字-margin-left
                .build();

        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();
        BufferedImage bufferedImage = builder.buildLegendGraphic(produceLayerList(), legendOptionsNew);

        int padding = 100;
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth()
                + padding * 2, bufferedImage.getHeight() + padding * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(bufferedImage, padding, padding, null);
        g.dispose();
        ImageIO.write(bufferedImage, "png", new FileOutputStream("data/legend/output/legend11.png"));
    }
}