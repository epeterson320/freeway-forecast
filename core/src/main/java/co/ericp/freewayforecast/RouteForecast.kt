package co.ericp.freewayforecast;

import co.ericp.freewayforecast.routes.Route
import co.ericp.freewayforecast.weather.WeatherPoint

/**
 * The main data structure of this API. This represents a route to travel
 * combined with a list of weather points along the way.
 */
data class RouteForecast(val route: Route, val weatherPoints: List<WeatherPoint>)
