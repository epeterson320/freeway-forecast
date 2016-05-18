package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.LatLng

/**
 * One step of a trip (one stretch of driving without turns)
 */
data class Step(
        val htmlInstructions: String,
        val distance: Double,
        val duration: Long,
        val start: LatLng,
        val end: LatLng,
        val polyline: List<LatLng>
)
