package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.routeForecast.Location

/**
 * One leg of a trip. Trips without waypoints only have one leg.
 */
data class Leg(
        val start: Location,
        val end: Location,
        val distance: Double,
        val duration: Long,
        val departureTime: Long,
        val arrivalTime: Long,
        val steps: List<Step>
)
