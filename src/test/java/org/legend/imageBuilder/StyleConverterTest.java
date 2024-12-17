package org.legend.imageBuilder;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.xml.styling.SLDParser;
import org.junit.Test;
import org.legend.utils.StyleConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StyleConverterTest {

    @Test
    public void test() {
        File file = new File("D:\\data\\project\\ankang\\安康地质图数据\\result\\T2侵入岩\\mapbox-style.json");
        JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
        String string = jsonObject.toString();
        String string1 = StyleConverter.convertMapboxStyle2SLD(string, StandardCharsets.UTF_8);
        System.out.println(string1);
        File file1 = new File("D:\\data\\project\\ankang\\安康地质图数据\\result\\T2侵入岩\\mapbox-style.sld");

        try {
            Files.write(Paths.get(file1.toURI()), string1.getBytes());  // 将字符串写入文件
            System.out.println("内容已写入文件！");
        } catch (IOException e) {
            e.printStackTrace();  // 捕获 IO 异常
        }

        StyleFactory styleFactory3 = CommonFactoryFinder.getStyleFactory();
        FeatureTypeStyle featureTypeStyle3 = styleFactory3.createFeatureTypeStyle();

        SLDParser styleReader = null;
        try {
            styleReader = new SLDParser(styleFactory3, file1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert styleReader != null;
        Style sld = styleReader.readXML()[0];

        sld.featureTypeStyles().add(featureTypeStyle3);
    }
}
