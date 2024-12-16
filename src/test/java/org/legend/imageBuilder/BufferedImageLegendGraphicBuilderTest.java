package org.legend.imageBuilder;

import junit.framework.TestCase;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.*;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.mbstyle.MapBoxStyle;
import org.geotools.xml.styling.SLDParser;
import org.junit.Test;
import org.legend.options.LegendOptions;
import org.legend.utils.LegendUtils;
import org.legend.utils.geotools.FeatureSourceType;
import org.legend.utils.geotools.FeatureSourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BufferedImageLegendGraphicBuilderTest extends TestCase {


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

    public static Style getStyle(String stylePath) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(stylePath);
            StyledLayerDescriptor sld = MapBoxStyle.parse(stream);
            NamedLayer l = null;
            for (int i = 0; i < sld.layers().size(); i++) {
                l = (NamedLayer) sld.layers().get(i);
            }
            Style style = l.getStyles()[0];
            return style;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return null;
    }


    @Test
    public void testBuildLegendGraphic() throws Exception {
        LegendOptions legendOptionsNew = LegendOptions.builder()
                .width(35) // rule-image宽度
                .height(35) // rule-image高度
                .transparent(true) // ？
                .bgColor("bgColor")
                .layout(LegendUtils.LegendLayout.VERTICAL)  // 布局方向
                .fontName("TimesRoman")
                .fontStyle("bold")
                .fontColor("fontColor")
                .fontSize(15)
                .labelXposition(0)  // 标题-margin-left
                .labelXOffset(0)  // rule-label-margin-left
                .maxHeight(200) // rules最大高度(而不是整个图例的高度，整个图例的宽度高度是自适应生成的。具体由image宽度高度、rules的margin、布局方向等参数决定)
                .title("图例")
                .build();
        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();

        //        File file2 = new File("D:\\data\\vector\\shp\\国土资源shp\\地类图斑_安康市.shp");
        File file2 = new File("D:\\data\\vector\\mbtiles\\linespaceOutPut\\planetiler\\dltb\\shanxi_dizhi\\dltb.mbtiles");

        FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceFromShp = FeatureSourceUtils.getFeatureSource(file2, FeatureSourceType.MBTILES);

        Style sld4 = getStyle("data/mbstyle/ankang-style.json");
//        Style sld4 = getSldStyle("C:\\Users\\admin\\Desktop\\安康1.sld");
//        Style sld4 = getSldStyle("data/mbstyle/test.sld");
        FeatureLayer layer4 = new FeatureLayer(featureSourceFromShp, sld4);

        List<FeatureLayer> layerList = new ArrayList<>();
        layerList.add(layer4);

        BufferedImage bufferedImage = builder.buildLegendGraphic(layerList, legendOptionsNew);
        ImageIO.write(bufferedImage, "png", new FileOutputStream("data/legend/output/legend.png"));
    }
}