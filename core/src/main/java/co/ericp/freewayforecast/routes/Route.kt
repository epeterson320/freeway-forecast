package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.Location

/**
 * A route at a particular time from a place to a place.
 */
class Route(
        val distance: Long, // Meters
        val duration: Long, // Unix time
        val startTime: Long,
        val endTime: Long,
        val neBound: Location,
        val swBound: Location,
        val origin: Location,
        val destination: Location,
        val legs: List<Leg>
)
