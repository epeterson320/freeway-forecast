package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.LatLng
import rx.Single

/**
 * A source for getting the weather.

 * An example implementation of this would be the weather.com API.
 */
interface WeatherSource {
    fun getForecast(coords: LatLng, time: Long): Single<Forecast>
}
