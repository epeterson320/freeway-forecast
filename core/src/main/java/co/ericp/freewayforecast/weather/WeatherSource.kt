package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.LatLon
import rx.Single

/**
 * A source for getting the weather.

 * An example implementation of this would be the weather.com API.
 */
interface WeatherSource {
    fun getForecast(coords: LatLon, time: Long): Single<Forecast>
}
