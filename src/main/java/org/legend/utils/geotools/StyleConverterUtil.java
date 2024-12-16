package org.legend.utils.geotools;

import org.geotools.api.style.*;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.mbstyle.MapBoxStyle;
import org.geotools.xml.styling.SLDParser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StyleConverterUtil {
    public static Style convertMapboxStyleString2Style(String stylePath) {
        try {
            InputStream stream = new FileInputStream(stylePath);
            StyledLayerDescriptor sld = MapBoxStyle.parse(stream);
            List<StyledLayer> layers = sld.layers();
            NamedLayer layer = null;
            for (int i = 0; i < layers.size(); i++) {
                layer = (NamedLayer) layers.get(i);
            }
            Style style = layer.getStyles()[0];
            stream.close();
            return style;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extract the style from sld file.
     *
     * @param sldFilePath the sld file path
     * @return the style
     */
    public static Style getSldStyle(String sldFilePath) throws IOException {
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
}
