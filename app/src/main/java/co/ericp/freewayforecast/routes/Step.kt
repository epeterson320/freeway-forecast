package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.routeForecast.Location

/**
 * One step of a trip (one stretch of driving without turns)
 */
data class Step(
        val htmlInstructions: String,
        val distance: Double, // meters
        val duration: Long, // milliseconds
        val start: Location,
        val end: Location,
        val polyline: List<Location>
)
