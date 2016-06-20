package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.LatLon

/**
 * A the weather at a specific time and place.
 */
data class WeatherPoint(
        val coords: LatLon,
        val time: Long,
        val temp: Double = 0.0,
        val status: Int = 0
)

