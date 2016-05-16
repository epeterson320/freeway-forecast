package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.LatLng

/**
 * The weather forecast over a given stretch of time in one place.
 */
data class Forecast(
        val coords: LatLng,
        val points: List<WeatherPoint>,
        val startTime: Long,
        val endTime: Long
)
