package co.ericp.freewayforecast

import co.ericp.freewayforecast.routes.Route
import co.ericp.freewayforecast.weather.WeatherPoint
import rx.Observable

/**
 * The main API to our business logic.
 *
 * This takes a route and returns a collection of weather points as they become
 * available.
 */
interface RouteForecastSource {
    fun getRouteForecasts(
            routes: List<Route>,
            departure: Long): Observable<RouteForecast>
}

