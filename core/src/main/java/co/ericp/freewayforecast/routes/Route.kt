package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.LatLon
import co.ericp.freewayforecast.Location

/**
 * A route at a particular time from a place to a place.
 */
class Route(
        val distance: Double,
        val duration: Long,
        val startTime: Long,
        val endTime: Long,
        val neBound: LatLon,
        val swBound: LatLon,
        val origin: Location,
        val destination: Location,
        val legs: List<Leg>
)
