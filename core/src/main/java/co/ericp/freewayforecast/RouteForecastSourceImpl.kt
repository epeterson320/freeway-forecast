package co.ericp.freewayforecast

import rx.Single
import java.util.Collections

class RouteForecastSourceImpl : RouteForecastSource {
    override fun getRouteForecast(
            origin: Location,
            destination: Location,
            departure: Long): Single<List<RouteForecast>> {
        return Single.just(Collections.emptyList());
    }
}
