package com.bluesierralabs.freewayforecast.Models;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by timothy on 12/31/14.
 */
public class mapModel {

    protected static mapModel mapInstance = null;

    private GoogleMap map;

    public mapModel() {
        // your init code...
    }

    public static mapModel getInstance() {
        if (mapInstance == null) {
            mapInstance = new mapModel();
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
