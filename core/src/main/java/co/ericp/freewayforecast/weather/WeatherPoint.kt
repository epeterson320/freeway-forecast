package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.LatLng

/**
 * A the weather at a specific time and place.
 */
data class WeatherPoint(
        val coords: LatLng,
        val time: Long,
        val temp: Double,
        val status: Int
)
