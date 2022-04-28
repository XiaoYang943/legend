package org.legend.imageBuilder;

import junit.framework.TestCase;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.FileUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.util.URLs;
import org.geotools.xml.styling.SLDParser;
import org.legend.model.Scale;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
        
        int padding = 100;
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth()
                + padding *2, bufferedImage.getHeight() + padding *2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(bufferedImage, padding, padding, null);
        g.dispose();
        ImageIO.write(bufferedImage,"png",new FileOutputStream("data/legend/legend2.png"));
    }

    public void testProduceMapWithLegend() throws Exception {
        //File inFile = new File("/home/adrien/data/geoserver/bdtopo_v2_Redon/building_indicators.geojson");
        File inFile = new File("/home/adrien/data/geoserver/bdtopo_v2_Redon/building.geojson");
        Map<String, Object> params = new HashMap<>();
        params.put(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(inFile));
        DataStore dataStore3 = DataStoreFinder.getDataStore(params);
        String[] typeNames3 = dataStore3.getTypeNames();
        String typeName3 = typeNames3[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource3 = dataStore3.getFeatureSource(typeName3);

        //Style sld = getSldStyle("data/sld/building_urban_typo.sld");
        Style sld = getSldStyle("data/sld/pop_grid_intervals.sld");
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
        //ImageIO.write(legendBufferedImage,"png",new FileOutputStream("data/legend/building_urban_typo_legend.png"));
        ImageIO.write(legendBufferedImage,"png",new FileOutputStream("data/legend/building_legend.png"));

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
        //ImageIO.write(mapBufferedImage, "png", new FileOutputStream("data/legend/building_indicators_map.png"));
        ImageIO.write(mapBufferedImage, "png", new FileOutputStream("data/legend/building_map.png"));

        //BufferedImage image = ImageIO.read(new File("data/legend/building_indicators_map.png"));
        BufferedImage image = ImageIO.read(new File("data/legend/building_map.png"));
        //BufferedImage legend = ImageIO.read(new File("data/legend/building_urban_typo_legend.png"));
        BufferedImage legend = ImageIO.read(new File("data/legend/building_legend.png"));

        // create the new image, canvas size is the max. of both image sizes
        int imgWidth = Math.max(image.getWidth(), legend.getWidth());
        int imgHeight = Math.max(image.getHeight(), legend.getHeight());
        BufferedImage combined = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.drawImage(legend, 10, image.getHeight()-300, null);

        // Generate the compass image
        BufferedImage compassBufIma = rasterize(new File("data/img/Rose_des_vents.svg"));
        g.drawImage(compassBufIma, image.getWidth()-150, image.getHeight()-150, null);

        // Generate the map scale
        Scale mapScale = new Scale(map,imgWidth);
        BufferedImage scaleBufferedImage = mapScale.paintMapScale();
        g.drawImage(scaleBufferedImage, image.getWidth()-500, image.getHeight()-150, null);

        g.dispose();

        // Save as new image
        //ImageIO.write(combined, "PNG", new File("data/legend/building_urban_typo_mapAndlegend.png"));
        ImageIO.write(combined, "PNG", new File("data/legend/building_mapAndlegend_withMarges.png"));
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static BufferedImage rasterize(File svgFile) throws IOException {

        final BufferedImage[] imagePointer = new BufferedImage[1];

        // Rendering hints can't be set programatically, so
        // we override defaults with a temporary stylesheet.
        // These defaults emphasize quality and precision, and
        // are more similar to the defaults of other SVG viewers.
        // SVG documents can still override these defaults.
        String css = "svg {" +
                "shape-rendering: geometricPrecision;" +
                "text-rendering:  geometricPrecision;" +
                "color-rendering: optimizeQuality;" +
                "image-rendering: optimizeQuality;" +
                "}";
        File cssFile = File.createTempFile("batik-default-override-", ".css");
        FileUtils.writeStringToFile(cssFile, css);

        TranscodingHints transcoderHints = new TranscodingHints();
        transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
        transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION,
                SVGDOMImplementation.getDOMImplementation());
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
                SVGConstants.SVG_NAMESPACE_URI);
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
        transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString());
        transcoderHints.put(ImageTranscoder.KEY_WIDTH, (float) 150);
        transcoderHints.put(ImageTranscoder.KEY_HEIGHT, (float) 150);

        try {
            TranscoderInput input = new TranscoderInput(new FileInputStream(svgFile));
            ImageTranscoder t = new ImageTranscoder() {
                @Override
                public BufferedImage createImage(int w, int h) {
                    return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                }

                @Override
                public void writeImage(BufferedImage image, TranscoderOutput out)
                        throws TranscoderException {
                    imagePointer[0] = image;
                }
            };
            t.setTranscodingHints(transcoderHints);
            t.transcode(input, null);
        }
        catch (TranscoderException ex) {
            // Requires Java 6
            ex.printStackTrace();
            throw new IOException("Couldn't convert " + svgFile);
        }
        finally {
            cssFile.delete();
        }
        return imagePointer[0];
    }

}