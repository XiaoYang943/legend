package org.legend.imageBuilder;

import junit.framework.TestCase;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.Style;
import org.geotools.map.FeatureLayer;
import org.junit.Test;
import org.legend.options.LegendOptions;
import org.legend.utils.LegendUtils;
import org.legend.utils.geotools.FeatureSourceType;
import org.legend.utils.geotools.FeatureSourceUtils;
import org.legend.utils.geotools.StyleConverterUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BufferedImageLegendGraphicBuilderTest extends TestCase {


    @Test
    public void testBuildLegendGraphic() throws Exception {
        LegendOptions legendOptions = LegendOptions.builder()
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

        Style sld4 = StyleConverterUtil.convertMapboxStyleString2Style("data/mbstyle/ankang-style.json");
//        Style sld4 = getSldStyle("C:\\Users\\admin\\Desktop\\安康1.sld");
//        Style sld4 = StyleConverterUtil.getSldStyle("data/mbstyle/test.sld");
        FeatureLayer layer4 = new FeatureLayer(featureSourceFromShp, sld4);

        List<FeatureLayer> layerList = new ArrayList<>();
        layerList.add(layer4);

        BufferedImage bufferedImage = builder.buildLegendGraphic(layerList, legendOptions);
        ImageIO.write(bufferedImage, "png", new FileOutputStream("data/legend/output/legend.png"));
    }
}