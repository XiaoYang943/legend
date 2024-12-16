package org.legend.utils.vector;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.mbtiles.MBTilesDataStoreFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FeatureSourceUtils {
    public static FeatureSource<SimpleFeatureType, SimpleFeature> getFeatureSource(File file, FeatureSourceType type) {
        switch (type) {
            case FeatureSourceType.SHP:
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("url", file.toURI().toString());
                    DataStore dataStore = DataStoreFinder.getDataStore(params);
                    String[] typeNames = dataStore.getTypeNames();
                    return dataStore.getFeatureSource(typeNames[0]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case MBTILES:
                try {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put(MBTilesDataStoreFactory.DBTYPE.key, "mbtiles");
                    params.put(MBTilesDataStoreFactory.DATABASE.key, file);
                    DataStore dataStore = DataStoreFinder.getDataStore(params);
                    String[] typeNames = dataStore.getTypeNames();
                    return dataStore.getFeatureSource(typeNames[0]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            default:
                return null;
        }
    }
}
