package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.routeForecast.Location

/**
 * A the weather at a specific time and place.
 */
data class WeatherPoint(
        val location: Location,
        val time: Long,
        val temp: Double = 0.0,
        val status: Status = Status.UNKNOWN
)

