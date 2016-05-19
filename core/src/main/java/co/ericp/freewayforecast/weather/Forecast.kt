package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.LatLon

/**
 * The weather forecast over a given stretch of time in one place.
 */
data class Forecast(
        val coords: LatLon,
        val points: List<WeatherPoint>,
        val startTime: Long,
        val endTime: Long
)
