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
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.StreamingRenderer;
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

//        // 2nd data source
//        File file = new File("data/shp/hedgerow/hedgerow.shp");
//        Map<String, String> connect = new HashMap();
//        connect.put("url", file.toURI().toString());
//        DataStore dataStore = DataStoreFinder.getDataStore(connect);
//        String[] typeNames = dataStore.getTypeNames();
//        String typeName = typeNames[0];
//        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore.getFeatureSource(typeName);
//        Style style = createStyle(featureSource,"hedgerow\nanotherline");
//        FeatureLayer layer = new FeatureLayer(featureSource, style);
//
//        // 3rd data source
//        File inFile = new File("/home/adrien/data/geoserver/bdtopo_v2_Redon/building.geojson");
//        Map<String, Object> params = new HashMap<>();
//        params.put(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(inFile));
//        DataStore dataStore3 = DataStoreFinder.getDataStore(params);
//        String[] typeNames3 = dataStore3.getTypeNames();
//        String typeName3 = typeNames3[0];
//        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource3 = dataStore3.getFeatureSource(typeName3);
//        Style sld = getSldStyle("data/sld/pop_grid_intervals.sld");
//        FeatureLayer layer3 = new FeatureLayer(featureSource3, sld);
//        //layer3.setTitle("population density");

        List<FeatureLayer> layerList = new ArrayList<>();
//        layerList.add(layer);
        layerList.add(layer2);
//        layerList.add(layer3);
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

        FilterFactory filterFactory2 = CommonFactoryFinder.getFilterFactory();

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
        legendOptions.put("labelXposition",0);
        legendOptions.put("labelXOffset",0);

        BufferedImageLegendGraphicBuilder builder = new BufferedImageLegendGraphicBuilder();
        BufferedImage bufferedImage = builder.buildLegendGraphic(produceLayerList(),legendOptions);
        
        int padding = 100;
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth()
                + padding *2, bufferedImage.getHeight() + padding *2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(bufferedImage, padding, padding, null);
        g.dispose();
        ImageIO.write(bufferedImage,"png",new FileOutputStream("data/legend/output/legend2.png"));
    }

    public void testOsmProjection() throws Exception {
        FeatureLayer layer = LayerUtils.buildLayer("/home/adrien/data/geojson/localisation_zone.geojson","data/sld/sld_zone_EDIT.sld");

        int width = 200;
        int height = 200;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();
        Rectangle rectangle = new Rectangle(width, height);
        System.out.println("layer.getBounds() : " + layer.getBounds());
        CoordinateReferenceSystem crs = CRS.decode("EPSG:3857", true);
        ReferencedEnvelope envelope = new ReferencedEnvelope(layer.getBounds(), crs);

        MapContent mapContent = new MapContent();
        mapContent.addLayer(layer);
//        mapContent.addLayer(new AsyncTileLayer(new OSMService("Mapnik", "http://tile.openstreetmap.org/")));

        final MapViewport viewport = mapContent.getViewport();
        viewport.setBounds(layer.getBounds().transform(crs, false));
        //viewport.setBounds(envelope);
        viewport.setScreenArea(rectangle);

        StreamingRenderer renderer = new StreamingRenderer();
        renderer.setMapContent(mapContent);
        renderer.paint(graphics, rectangle, envelope);

        ImageIO.write(image, "png", new File("data/legend/brandenWithData.png"));
    }
}