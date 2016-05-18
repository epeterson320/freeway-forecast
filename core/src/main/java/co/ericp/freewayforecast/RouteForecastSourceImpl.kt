package co.ericp.freewayforecast

import co.ericp.freewayforecast.LocationQuery
import co.ericp.freewayforecast.routes.RouteSource
import co.ericp.freewayforecast.weather.WeatherSource
import rx.Single
import java.util.Collections

class RouteForecastSourceImpl(
        val routeSource: RouteSource,
        val weatherSource: WeatherSource) : RouteForecastSource {

    override fun getRouteForecast(
            origin: LocationQuery,
            destination: LocationQuery,
            departure: Long): Single<List<RouteForecast>> {
        return Single.just(Collections.emptyList());
    }
}
