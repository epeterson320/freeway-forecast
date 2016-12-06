package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.Location

/**
 * One step of a trip (one stretch of driving without turns)
 */
data class Step(
        val htmlInstructions: String,
        val distance: Long, // meters
        val duration: Long, // milliseconds
        val start: Location,
        val end: Location,
        val polyline: List<Location>
)
