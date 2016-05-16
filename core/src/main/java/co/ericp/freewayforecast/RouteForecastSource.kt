package co.ericp.freewayforecast

import rx.Single

/**
 * The main API to our business logic.
 *
 * This takes a start and end location and departure time as parameters
 * and returns a list of routes, along with their weather conditions along the
 * way.
 */
interface RouteForecastSource {
    fun getRouteForecast(
            origin: Location,
            destination: Location,
            departure: Long): Single<List<RouteForecast>>
}