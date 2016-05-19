package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.LatLon

/**
 * One step of a trip (one stretch of driving without turns)
 */
data class Step(
        val htmlInstructions: String,
        val distance: Double,
        val duration: Long,
        val start: LatLon,
        val end: LatLon,
        val polyline: List<LatLon>
)
