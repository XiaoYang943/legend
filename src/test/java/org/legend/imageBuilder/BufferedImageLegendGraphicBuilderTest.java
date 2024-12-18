package org.legend.imageBuilder;

import cn.hutool.json.JSONUtil;
import junit.framework.TestCase;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.filter.expression.Literal;
import org.geotools.api.style.*;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.mbstyle.MBStyle;
import org.geotools.mbstyle.layer.FillMBLayer;
import org.geotools.mbstyle.layer.MBLayer;
import org.geotools.mbstyle.sprite.SpriteGraphicFactory;
import org.geotools.styling.SLD;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.legend.options.LegendOptions;
import org.legend.utils.legend.BufferedImageLegendGraphicBuilder;
import org.legend.utils.legend.LegendUtils;
import org.legend.utils.vector.FeatureSourceType;
import org.legend.utils.vector.FeatureSourceUtils;
import org.legend.utils.vector.StyleConverterUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BufferedImageLegendGraphicBuilderTest extends TestCase {

    @Test
    public void testBuildLegendGraphicByFillColor() throws Exception {
        LegendOptions legendOptions = LegendOptions.builder()
                .width(32)
                .height(32)
                .transparent(true)
                .bgColor("#FFFFFF")
                .layout(LegendUtils.LegendLayout.VERTICAL)
                .fontName("TimesRoman")
                .fontStyle("bold")
                .fontColor("fontColor")
                .fontSize(12)
                .titleOffsetX(0)
                .labelOffsetX(20)
                .ruleOffsetY(5)
                .title("图例")
                .build();
        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();

        File file2 = new File("D:\\data\\vector\\mbtiles\\linespaceOutPut\\planetiler\\dltb\\shanxi_dizhi\\dltb.mbtiles");
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceFromShp = FeatureSourceUtils.getFeatureSource(file2, FeatureSourceType.MBTILES);


        Style sld4 = StyleConverterUtil.getSldStyle("C:\\Users\\admin\\Desktop\\安康1.sld");

        FeatureLayer layer4 = new FeatureLayer(featureSourceFromShp, sld4);

        List<FeatureLayer> layerList = new ArrayList<>();
        layerList.add(layer4);

        BufferedImage bufferedImage = builder.buildLegendGraphic(layerList, legendOptions);
        ImageIO.write(bufferedImage, "png", new FileOutputStream("data/legend/output/legend.png"));
    }

    @Test
    public void testBuildLegendGraphic() throws Exception {
        LegendOptions legendOptions = LegendOptions.builder()
                .width(64)
                .height(64)
                .transparent(true) // ？
                .bgColor("bgColor")
                .layout(LegendUtils.LegendLayout.VERTICAL)
                .fontName("TimesRoman")
                .fontStyle("bold")
                .fontColor("fontColor")
                .fontSize(15)
                .titleOffsetX(0)
                .labelOffsetX(0)
                .maxHeight(200)
                .title("图例")
                .isShowAllRules(false)
                .build();
        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();

        File file2 = new File("D:\\data\\project\\ankang\\安康地质图数据\\result\\T2侵入岩\\ankang-dizhi-qinruyan\\ankang-dizhi-qinruyan.mbtiles");

        FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceFromShp = FeatureSourceUtils.getFeatureSource(file2, FeatureSourceType.MBTILES);

        String styleFilePath = "D:\\data\\project\\ankang\\安康地质图数据\\result\\T2侵入岩\\qinruyan-mapbox-multi.json";
        Style sld4 = StyleConverterUtil.convertMapboxStyleString2Style(styleFilePath);
//        Style sld4 = StyleConverterUtil.getSldStyle("data/mbstyle/test.sld");

//        MBStyle mbStyle = fillSprite(styleFilePath);
        FeatureLayer layer4 = new FeatureLayer(featureSourceFromShp, sld4);

        List<FeatureLayer> layerList = new ArrayList<>();
        layerList.add(layer4);


        String json = new String(Files.readAllBytes(Paths.get(styleFilePath)));
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(json);
        org.json.simple.JSONObject jsonObject = (JSONObject) obj;
        MBStyle mbStyle = new MBStyle(jsonObject);


        cn.hutool.json.JSONObject spriteJsonObject = JSONUtil.readJSONObject(new File("D:\\data\\project\\ankang\\安康地质图数据\\result\\T2侵入岩\\sprite.json"), StandardCharsets.UTF_8);


        BufferedImage bufferedImage = builder.buildLegendGraphic(layerList, legendOptions);
        ImageIO.write(bufferedImage, "png", new FileOutputStream("data/legend/output/legend.png"));
    }

    @Test
    public void testReadMBStyle() {
        try {
            String filePath = "D:\\data\\project\\ankang\\安康地质图数据\\result\\T2侵入岩\\qinruyan-mapbox-multi.json";
            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(json);
            org.json.simple.JSONObject jsonObject = (JSONObject) obj;
            MBStyle mbStyle = new MBStyle(jsonObject);
            List<MBLayer> layers = mbStyle.layers();
//            layers.getFirst().getPaint().get("fill-pattern").toString()
            for (MBLayer layer : layers) {
                if (layer instanceof FillMBLayer) {
                    FillMBLayer mbFill = (FillMBLayer) layer;
                    List<FeatureTypeStyle> fts = mbFill.transform(mbStyle);
                    FeatureTypeStyle featureTypeStyle = fts.get(0);
                    List<Rule> rules = featureTypeStyle.rules();
                    PolygonSymbolizer psym = SLD.polySymbolizer(featureTypeStyle);
                    Graphic g = psym.getFill().getGraphicFill();
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void testReadFillPattern() {
        String filePath = "data/mbstyle/qinruyan.json";
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(json);
            org.json.simple.JSONObject jsonObject = (JSONObject) obj;
            MBStyle mbStyle = new MBStyle(jsonObject);
            List<MBLayer> layers = mbStyle.layers();
            for (MBLayer layer : layers) {
                if (layer instanceof FillMBLayer) {
                    FillMBLayer mbFill = (FillMBLayer) layer;
                    JSONObject paint = mbFill.getPaint();
                    Object fillPattern = paint.get("fill-pattern");
                    if (fillPattern instanceof String) {

                    } else if (fillPattern instanceof org.json.simple.JSONArray) {
                        getFillPatternBeanFromJSONArray((org.json.simple.JSONArray) fillPattern);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public SpriteGraphicFactory mgf = new SpriteGraphicFactory();

    private void getFillPatternBeanFromJSONArray(JSONArray jsonArray) {
        Object first = jsonArray.get(0);
        if (first.equals("match")) {
            JSONArray second = (JSONArray) jsonArray.get(1);
            System.out.println(jsonArray.size());
            boolean hasDefaultSpriteId;
            if (jsonArray.size() % 2 != 0) {
                // 奇数
                hasDefaultSpriteId = true;
            } else {
                // 偶数
                hasDefaultSpriteId = false;
            }
            if (hasDefaultSpriteId) {
                final FilterFactory FF = CommonFactoryFinder.getFilterFactory();

                String urlStr = "http://localhost:13002/scenemap/services/vector/mbtiles/ankang-qinruyan/style/sprite";

                for (int i = 2; i < jsonArray.size() - 1; i += 2) {
//                    System.out.println(jsonArray.get(i));
                    String spritePropertyName = (String) jsonArray.get(i);
                    String spritePropertyValue = (String) jsonArray.get(i + 1);
//                    System.out.println(spritePropertyName + "_" + spritePropertyValue);

                    Literal spriteExpression = FF.literal(urlStr + "#" + spritePropertyValue);
                    SpriteGraphicFactory factory = new SpriteGraphicFactory();
                    try {
                        Icon icon = factory.getIcon(null, spriteExpression, "mbsprite", 15);
                        System.out.println(icon.getIconWidth());

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                String spriteDefaultPropertyValue = (String) jsonArray.getLast();
            }

        }
    }

    public void testGetSpriteIcon() {
        try {
            String urlStr = "http://localhost:13002/scenemap/services/vector/mbtiles/ankang-qinruyan/style/sprite";
            final FilterFactory FF = CommonFactoryFinder.getFilterFactory();
            Literal spriteExpression = FF.literal(urlStr + "#" + "fill_0_00AF00FF_00AF00FF_100");
            SpriteGraphicFactory factory = new SpriteGraphicFactory();
            Icon icon = factory.getIcon(null, spriteExpression, "mbsprite", 128);
            assertNotNull(icon);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testPaintSpriteIcon() {
        String urlStr = "http://localhost:13002/scenemap/services/vector/mbtiles/ankang-qinruyan/style/sprite";
        final FilterFactory FF = CommonFactoryFinder.getFilterFactory();
        Literal spriteExpression = FF.literal(urlStr + "#" + "fill_125_99_");
        SpriteGraphicFactory factory = new SpriteGraphicFactory();
        try {

            Icon icon = factory.getIcon(null, spriteExpression, "mbsprite", 128);

            // 创建一个 BufferedImage 用来绘制图标
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // 获取 Graphics2D 对象
            Graphics2D g2d = bufferedImage.createGraphics();

            // 绘制图标到图片上
            icon.paintIcon(null, g2d, 0, 0);

            // 释放 Graphics2D 资源
            g2d.dispose();

            // 输出文件路径
            File outputfile = new File("data/legend/output/output_image.png");

            // 保存为 PNG 图片
            ImageIO.write(bufferedImage, "PNG", outputfile);
            System.out.println("Image saved to: " + outputfile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testGetSpriteObjectFromJSON() {
        cn.hutool.json.JSONObject jsonObject = JSONUtil.readJSONObject(new File("D:\\data\\project\\ankang\\安康地质图数据\\result\\T2侵入岩\\sprite.json"), StandardCharsets.UTF_8);
        Object o = jsonObject.get("fill_0_00AF00FF_00AF00FF_100");
        System.out.println(o.toString());
    }
}