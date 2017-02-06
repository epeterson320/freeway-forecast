package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.routeForecast.Location
import io.reactivex.Observable

/**
 * A source for getting the weather.

 * An example implementation of this would be the weather.com API.
 */
interface WeatherSource {
    fun getForecast(location: Location,
                    time: Long = System.currentTimeMillis(),
                    until: Long = time + 24 * 3600 * 1000): Observable<WeatherPoint>
}
