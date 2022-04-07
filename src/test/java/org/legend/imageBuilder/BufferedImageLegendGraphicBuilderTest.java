package org.legend.imageBuilder;

import com.sun.xml.bind.v2.TODO;
import junit.framework.TestCase;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.Renderer;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.util.URLs;
import org.geotools.xml.styling.SLDParser;
import org.legend.model.Legend;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
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

    float point_OPACITY = 0.1f;

    public List<FeatureLayer> produceLayerList() throws Exception {
        // first data source
        File file2 = new File("data/shp/landcover2000/landcover2000.shp");
        Map<String, String> connect2 = new HashMap<>();
        connect2.put("url", file2.toURI().toString());
        DataStore dataStore2 = DataStoreFinder.getDataStore(connect2);
        String[] typeNames2 = dataStore2.getTypeNames();
        String typeName2 = typeNames2[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource2 = dataStore2.getFeatureSource(typeName2);
        Style style2 = createStyle(featureSource2, "landcover2000");
        FeatureLayer layer2 = new FeatureLayer(featureSource2, style2);

        // 2nd data source
        File file = new File("data/shp/hedgerow/hedgerow.shp");
        Map<String, String> connect = new HashMap();
        connect.put("url", file.toURI().toString());
        DataStore dataStore = DataStoreFinder.getDataStore(connect);
        String[] typeNames = dataStore.getTypeNames();
        String typeName = typeNames[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore.getFeatureSource(typeName);
        Style style = createStyle(featureSource,"hedgerow\nanotherline");
        FeatureLayer layer = new FeatureLayer(featureSource, style);

        // 3rd data source
        File inFile = new File("/home/adrien/data/geoserver/bdtopo_v2_Redon/building.geojson");
        Map<String, Object> params = new HashMap<>();
        params.put(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(inFile));
        DataStore dataStore3 = DataStoreFinder.getDataStore(params);
        String[] typeNames3 = dataStore3.getTypeNames();
        String typeName3 = typeNames3[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource3 = dataStore3.getFeatureSource(typeName3);
        Style sld = getSldStyle("data/sld/pop_grid_intervals.sld");
        FeatureLayer layer3 = new FeatureLayer(featureSource3, sld);
        //layer3.setTitle("population density");

        List<FeatureLayer> layerList = new ArrayList<>();
        layerList.add(layer);
        layerList.add(layer2);
        layerList.add(layer3);
        return layerList;
    }

    /**
     * Creates a new Rule containing a Symbolizer tailored to the geometry type of the features that we are displaying.
     *
     * @param fillColor the fill color
     * @param geomType the geometry type
     * @return the buffered image
     */
    private Rule createRule(Color fillColor, Geometries geomType) {
        Symbolizer symbolizer = null;
        Fill fill;

        // Factories that we use to create style and filter objects
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();

        FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();

        float LINE_WIDTH = 1.0f;
        Stroke stroke = styleFactory.createStroke(filterFactory2.literal(Color.green), filterFactory2.literal(LINE_WIDTH));
        switch (geomType) {
            case MULTIPOLYGON:
                float OPACITY = 1.0f;
                fill = styleFactory.createFill(filterFactory2.literal(fillColor), filterFactory2.literal(OPACITY));
                symbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);
                break;

            case MULTILINESTRING:
                symbolizer = styleFactory.createLineSymbolizer(stroke, null);
                break;

            case POINT:
                fill = styleFactory.createFill(filterFactory2.literal(fillColor), filterFactory2.literal(point_OPACITY));

                Mark mark = styleFactory.getCircleMark();
                mark.setFill(fill);
                mark.setStroke(stroke);

                Graphic graphic = styleFactory.createDefaultGraphic();
                graphic.graphicalSymbols().clear();
                graphic.graphicalSymbols().add(mark);
                float POINT_SIZE = 10.0f;
                graphic.setSize(filterFactory2.literal(POINT_SIZE));

                symbolizer = styleFactory.createPointSymbolizer(graphic, null);
        }

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    /**
     * Creates a style.
     *
     * @param featureSource the data
     * @param ruleName the rule name
     * @return the style
     */
    private Style createStyle(FeatureSource<SimpleFeatureType, SimpleFeature> featureSource, String ruleName){
        SimpleFeatureType schema = featureSource.getSchema();
        GeometryDescriptor desc = schema.getGeometryDescriptor();
        Class<? extends Geometry> clazz = (Class<? extends Geometry>) desc.getType().getBinding();
        Geometries geomType = Geometries.getForBinding(clazz);
        // Create a basic Style to render the features
        Color fillColor = Color.darkGray;
        Rule rule = createRule(fillColor, geomType);
        rule.setName(ruleName);

        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        featureTypeStyle.rules().add(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
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

    public void testBuildLegendGraphic() throws Exception {
        Map legendOptions =new HashMap<>();
        legendOptions.put("width",35); // default is 50
        legendOptions.put("height",35); // default is 50
        //legendOptions.put("forceRuleLabelsOff","on");
        legendOptions.put("transparent","on"); // default is off
        legendOptions.put("bgColor",Color.ORANGE); // default is Color.WHITE;
        // Set the space between the image and the rule label
        legendOptions.put("ruleLabelMargin",0); //default is 3;
        legendOptions.put("verticalRuleMargin",5); //default is 0;
        legendOptions.put("horizontalRuleMargin",5); //default is 0;
        legendOptions.put("layout","HORIZONTAL"); //default is VERTICAL;
        legendOptions.put("verticalMarginBetweenLayers", 25); //default is 0;
        legendOptions.put("horizontalMarginBetweenLayers", 25); //default is 0;
        legendOptions.put("fontName", "TimesRoman"); //default is "Sans-Serif"
        legendOptions.put("fontStyle", "bold");
        legendOptions.put("fontColor",Color.BLUE); // default is Color.BLACK;
        legendOptions.put("fontSize","14"); // default is 12;

        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();
        BufferedImage bufferedImage = builder.buildLegendGraphic(produceLayerList(),legendOptions);
        ImageIO.write(bufferedImage,"png",new FileOutputStream("data/legend/legend.png"));
    }

    public void testProduceMapWithLegend() throws Exception {
        File inFile = new File("/home/adrien/data/geoserver/bdtopo_v2_Redon/building_indicators.geojson");
        Map<String, Object> params = new HashMap<>();
        params.put(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(inFile));
        DataStore dataStore3 = DataStoreFinder.getDataStore(params);
        String[] typeNames3 = dataStore3.getTypeNames();
        String typeName3 = typeNames3[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource3 = dataStore3.getFeatureSource(typeName3);
        Style sld = getSldStyle("data/sld/building_urban_typo.sld");
        FeatureLayer layer3 = new FeatureLayer(featureSource3, sld);
        //layer3.setTitle("population density");

        List<FeatureLayer> layerList = new ArrayList<>();
        layerList.add(layer3);

        MapContent map = new MapContent();
        map.addLayer(layer3);

        Map legendOptions =new HashMap<>();
        legendOptions.put("width",35); // default is 50
        legendOptions.put("height",35); // default is 50
        //legendOptions.put("forceRuleLabelsOff","on");
        legendOptions.put("transparent","on"); // default is off
        legendOptions.put("bgColor","#33ccff"); // default is Color.WHITE;
        // Set the space between the image and the rule label
        legendOptions.put("ruleLabelMargin",0); //default is 3;
        legendOptions.put("verticalRuleMargin",5); //default is 0;
        legendOptions.put("horizontalRuleMargin",5); //default is 0;
        //legendOptions.put("layout","HORIZONTAL"); //default is VERTICAL;
        legendOptions.put("verticalMarginBetweenLayers", 25); //default is 0;
        legendOptions.put("horizontalMarginBetweenLayers", 25); //default is 0;
        legendOptions.put("fontName", "TimesRoman"); //default is "Sans-Serif"
        legendOptions.put("fontStyle", "bold");
        legendOptions.put("fontColor",Color.BLUE); // default is Color.BLACK;
        legendOptions.put("fontSize","14"); // default is 12;

        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();
        BufferedImage legendBufferedImage = builder.buildLegendGraphic(layerList,legendOptions);
        ImageIO.write(legendBufferedImage,"png",new FileOutputStream("data/legend/building_urban_typo_legend.png"));

        GTRenderer renderer = new StreamingRenderer();
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        renderer.setJava2DHints(hints);
        Map<String, Object> rendererParams = new HashMap<>();
        LabelCacheImpl labelCache = new LabelCacheImpl();
        rendererParams.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
        //rendererParams.put(StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY,StreamingRenderer.SCALE_ACCURATE);
        rendererParams.put(StreamingRenderer.LINE_WIDTH_OPTIMIZATION_KEY, Boolean.FALSE);
        rendererParams.put(StreamingRenderer.ADVANCED_PROJECTION_HANDLING_KEY, true);
        //rendererParams.put(StreamingRenderer.CONTINUOUS_MAP_WRAPPING, true);
        renderer.setRendererHints(rendererParams);
        renderer.setMapContent(map);

        Rectangle imageBounds;
        ReferencedEnvelope mapBounds;
        try {
            mapBounds = map.getMaxBounds();
            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            imageBounds = new Rectangle(
                    0, 0, 1000, (int) Math.round(1000 * heightToWidth));

        } catch (Exception e) {
            // failed to access map layers
            throw new RuntimeException(e);
        }
        BufferedImage mapBufferedImage = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D gr = mapBufferedImage.createGraphics();
        gr.setPaint(Color.WHITE);
        gr.fill(imageBounds);
        renderer.paint(gr, imageBounds, mapBounds);
        ImageIO.write(mapBufferedImage, "png", new FileOutputStream("data/legend/building_indicators_map.png"));


        BufferedImage image = ImageIO.read(new File("data/legend/building_indicators_map.png"));
        BufferedImage legend = ImageIO.read(new File("data/legend/building_urban_typo_legend.png"));

        // create the new image, canvas size is the max. of both image sizes
        int w = Math.max(image.getWidth(), legend.getWidth());
        int h = Math.max(image.getHeight(), legend.getHeight());
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.drawImage(legend, 0, 0, null);

        g.dispose();

        // Save as new image
        ImageIO.write(combined, "PNG", new File("data/legend/building_indicators_mapAndlegend.png"));
    }

}