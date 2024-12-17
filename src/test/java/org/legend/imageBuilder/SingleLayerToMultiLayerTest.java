package org.legend.imageBuilder;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import org.geotools.mbstyle.MBStyle;
import org.geotools.mbstyle.layer.FillMBLayer;
import org.geotools.mbstyle.layer.MBLayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SingleLayerToMultiLayerTest {

    @Test
    public void test() {
        cn.hutool.json.JSONObject resJsonObject = JSONUtil.readJSONObject(new File("data/mbstyle/mapbox-style.json"), StandardCharsets.UTF_8);
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/mbstyle/mapbox-style.json")));
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
                        JSONArray layersArr = getFillPatternBeanFromJSONArray((JSONArray) fillPattern);

                        cn.hutool.json.JSONArray layersRes = (cn.hutool.json.JSONArray) resJsonObject.get("layers");
                        layersRes.clear();
                        layersRes.addAll(layersArr);
                        resJsonObject.set("layers", layersRes);
                        FileUtil.writeUtf8String(resJsonObject.toString(), "D:\\data\\project\\ankang\\安康地质图数据\\result\\T2侵入岩\\qinruyan-mapbox-multi.json");
                    }
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONArray getFillPatternBeanFromJSONArray(JSONArray jsonArray) {
        Object first = jsonArray.get(0);
        if (first.equals("match")) {
            JSONArray filter = (JSONArray) jsonArray.get(1);
            String filterName = filter.get(1).toString();
            JSONArray layerRes = new JSONArray();
            for (int i = 2; i < jsonArray.size() - 1; i += 2) {
                String spritePropertyName = (String) jsonArray.get(i);
                String spritePropertyValue = (String) jsonArray.get(i + 1);

                JSONObject jsonObject = new JSONObject();

                JSONArray jsonArray1 = new JSONArray();
                JSONArray jsonArray2 = new JSONArray();
                jsonArray2.add("==");
                jsonArray2.add("Geobody_Name");
                jsonArray2.add(spritePropertyName);
                jsonArray1.add("all");
                jsonArray1.add(jsonArray2);

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("fill-pattern", spritePropertyValue);

                jsonObject.put("id", spritePropertyName);
                jsonObject.put("type", "fill");
                jsonObject.put("source", "安康地质图_地质_2侵入岩");
                jsonObject.put("source-layer", "linespace_layer");
                jsonObject.put("filter", jsonArray1);
                jsonObject.put("paint", jsonObject1);

                layerRes.add(jsonObject);
            }
            return layerRes;
        }
        return null;
    }
}
