package com.bluesierralabs.freewayforecast.Models;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by timothy on 12/31/14.
 */
public class MapModel {

    protected static MapModel mapInstance = null;

    private GoogleMap map;

    public MapModel() {
        // your init code...
    }

    public static MapModel getInstance() {
        if (mapInstance == null) {
            mapInstance = new MapModel();
        }

        return mapInstance;
    }

    public void setMap(GoogleMap newMap) {
        this.map = newMap;
    }

    public GoogleMap getMap() {
        return map;
    }
}
