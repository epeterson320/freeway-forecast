package com.bluesierralabs.freewayforecast.models;

import com.google.maps.model.DirectionsRoute;

/**
 * Singleton to hold routes.  Ideally this would be passed through
 * normal intents but I don't want to make "routes" a parcelable
 * or bundle or such.
 */
public class Routes {

    private static DirectionsRoute[] mRoutes;

    public static void setRoutes(DirectionsRoute[] routes){
        mRoutes = routes;
    }

    public static DirectionsRoute[] getRoutes(){
        return mRoutes;
    }
}