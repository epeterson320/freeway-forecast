package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.Location

/**
 * A route at a particular time from a place to a place.
 */
class Route(
        val summary: String,
        val distance: Double, // Meters
        val duration: Long, // Milliseconds
        val startTime: Long, // Unix time (millis)
        val endTime: Long,
        val neBound: Location,
        val swBound: Location,
        val origin: Location,
        val destination: Location,
        val legs: List<Leg>
)
