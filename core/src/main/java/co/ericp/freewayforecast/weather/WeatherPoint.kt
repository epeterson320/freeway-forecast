package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.LatLon

/**
 * A the weather at a specific time and place.
 */
data class WeatherPoint(
        val coords: LatLon,
        val time: Long,
        val temp: Double,
        val status: Int
)
