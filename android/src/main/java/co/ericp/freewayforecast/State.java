package co.ericp.freewayforecast;

import java.util.List;

import co.ericp.freewayforecast.routes.Route;

/**
 * Singleton to hold routes.  Ideally this would be passed through
 * normal intents but I don't want to make "routes" a parcelable
 * or bundle or such.
 */
public class State {

    private static List<Route> routes;
    private static RouteForecast forecast;

    public static void setRoutes(List<Route> routes) {
        State.routes = routes;
    }

    public static List<Route> getRoutes(){
        return routes;
    }

    public static void setForecast(RouteForecast forecast) {
        State.forecast = forecast;
    }

    public static RouteForecast getForecast() {
        return forecast;
    }
}