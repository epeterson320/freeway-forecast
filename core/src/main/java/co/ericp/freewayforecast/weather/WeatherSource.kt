package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.Location
import io.reactivex.Observable

/**
 * A source for getting the weather.

 * An example implementation of this would be the weather.com API.
 */
interface WeatherSource {
    fun getForecast(location: Location,
                    time: Long,
                    until: Long? = null): Observable<WeatherPoint>
}
