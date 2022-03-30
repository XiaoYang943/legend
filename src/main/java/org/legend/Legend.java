package org.legend;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.legend.model.LegendItem;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Legend {

    private static String geometryAttributeName;
    float point_OPACITY = 0.1f;

    public static void main(String[] args) throws Exception {
        Legend me = new Legend();
        me.displayShapefile();
    }

    /**
     * Prompts the user for one or more shapefiles; then creates a simple Style and displays
     * the shapefiles and a map legend on screen
     */
    private void displayShapefile() throws Exception {

        // Create a map context and add our shapefile to it
        MapContent map = new MapContent();
        map.setTitle("LegendLab");

        File file2 = new File("/home/adrien/data/landcover2000.shp");
        Map<String, String> connect2 = new HashMap<>();
        connect2.put("url", file2.toURI().toString());
        DataStore dataStore2 = DataStoreFinder.getDataStore(connect2);
        String[] typeNames2 = dataStore2.getTypeNames();
        String typeName2 = typeNames2[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource2 = dataStore2.getFeatureSource(typeName2);
        SimpleFeatureType schema2 = featureSource2.getSchema();
        GeometryDescriptor desc2 = schema2.getGeometryDescriptor();
        Class<? extends Geometry> clazz2 = (Class<? extends Geometry>) desc2.getType().getBinding();
        Geometries geomType2 = Geometries.getForBinding(clazz2);
        // Create a basic Style to render the features
        Color fillColor2 = Color.darkGray;
        Rule rule2 = createRule(Color.green, fillColor2, geomType2);
        StyleFactory styleFactory2 = CommonFactoryFinder.getStyleFactory();
        FeatureTypeStyle featureTypeStyle2 = styleFactory2.createFeatureTypeStyle();
        featureTypeStyle2.rules().add(rule2);
        Style style2 = styleFactory2.createStyle();
        style2.featureTypeStyles().add(featureTypeStyle2);
        FeatureLayer layer2 = new FeatureLayer(featureSource2, style2);
        map.addLayer(layer2);
/*
        File inFile = new File("/home/ian/Data/states/states.geojson");
        Map<String, Object> params = new HashMap<>();
        params.put(GeoJSONDataStoreFactory.URLP.key, URLs.fileToUrl(inFile));
        DataStore newDataStore = DataStoreFinder.getDataStore(params);*/

        // Initialize request for producing legend
        final String outputFormat = "image/png";

        Map legendOptions =new HashMap<>();
        legendOptions.put("width",100);
        legendOptions.put("height",100);

        LegendItem legendElement = new LegendItem(layer2,legendOptions);
        BufferedImage bufferedImage = legendElement.produceBufferedImage();
        System.out.println(bufferedImage);
        ImageIO.write(bufferedImage,"png",new FileOutputStream(new File("/tmp/legendes/fichier3.png")));
    }

    /**
     * Helper for createXXXStyle methods. Creates a new Rule containing a Symbolizer tailored to the
     * geometry type of the features that we are displaying.
     */
    private Rule createRule(Color outlineColor, Color fillColor, Geometries geomType) throws FileNotFoundException {
        Symbolizer symbolizer = null;
        Fill fill = null;

        // Factories that we use to create style and filter objects
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
/*
        SLDParser stylereader = new SLDParser(styleFactory, new File("/home/ebocher/Autres/codes/geoclimate/geoindicators/src/main/resources/styles/lcz_primary.sld"));
        Style sld = stylereader.readXML()[0];*/

        FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();

        float LINE_WIDTH = 1.0f;
        Stroke stroke = styleFactory.createStroke(filterFactory2.literal(outlineColor), filterFactory2.literal(LINE_WIDTH));
        //GeometryDescriptor desc = schema.getGeometryDescriptor();
        switch (geomType) {
            case MULTIPOLYGON:
                float OPACITY = 1.0f;
                fill = styleFactory.createFill(filterFactory2.literal(fillColor), filterFactory2.literal(OPACITY));
                symbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
                break;

            case MULTILINESTRING:
                symbolizer = styleFactory.createLineSymbolizer(stroke, geometryAttributeName);
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

                symbolizer = styleFactory.createPointSymbolizer(graphic, geometryAttributeName);
        }

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

}
