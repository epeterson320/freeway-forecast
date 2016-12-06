package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.Location

/**
 * One leg of a trip. Trips without waypoints only have one leg.
 */
data class Leg(
        val start: Location,
        val end: Location,
        val distance: Long,
        val duration: Long,
        val steps: List<Step>
)
