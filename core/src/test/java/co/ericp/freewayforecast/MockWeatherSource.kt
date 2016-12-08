package co.ericp.freewayforecast

import co.ericp.freewayforecast.weather.WeatherPoint
import co.ericp.freewayforecast.weather.WeatherSource
import io.reactivex.Observable

class MockWeatherSource : WeatherSource {
    val minute = 60 * 1000

    override fun getForecast(location: Location,
                             time: Long,
                             until: Long?): Observable<WeatherPoint> =
            Observable.fromArray(
                    WeatherPoint(location, time + 0 * minute, 20.0),
                    WeatherPoint(location, time + 60 * minute, 20.0),
                    WeatherPoint(location, time + 120 * minute, 20.0))
}