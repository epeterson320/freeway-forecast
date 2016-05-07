package co.ericp.freewayforecast;

import java.util.List;

/**
 * The main data structure of this API. This represents a route to travel
 * combined with a list of weather points along the way.
 */
public class RouteForecast {
    co.ericp.freewayforecast.routes.Route route;
    List<WeatherPoint> weatherPoints;
}
