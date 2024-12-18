package org.legend.utils.style;

import org.geotools.api.style.StyledLayerDescriptor;
import org.geotools.mbstyle.MBStyle;
import org.geotools.xml.styling.SLDTransformer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

/**
 * 样式转换器
 */
public class StyleConverter {
    /**
     * Mapbox样式转换为SLD样式
     *
     * @param mapboxStyleStr Mapbox样式字符串
     * @param charset        字符集
     * @return SLD样式字符串
     */
    public static String convertMapboxStyle2SLD(String mapboxStyleStr, Charset charset) {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse(mapboxStyleStr);
            JSONObject jsonObject = (JSONObject) obj;
            MBStyle mbStyle = new MBStyle(jsonObject);
            StyledLayerDescriptor styledLayerDescriptor = mbStyle.transform();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SLDTransformer transformer = new SLDTransformer();
            transformer.setEncoding(charset);   // 设置字符集
            transformer.transform(styledLayerDescriptor, bos);
            return bos.toString();
        } catch (ParseException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

}
