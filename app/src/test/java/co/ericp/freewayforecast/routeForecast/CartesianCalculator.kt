package co.ericp.freewayforecast.routeForecast

import co.ericp.freewayforecast.routeForecast.DistanceCalculator
import co.ericp.freewayforecast.routeForecast.Location

/**
 * Distance calculator that treats latitude and longitude like y and x,
 * respectively, on a Cartesian plane. Used for predictable calculations in
 * tests. Not intended for production use.
 */
object CartesianCalculator : DistanceCalculator {
    override fun dist(from: Location, to: Location): Double {
        val dx = to.lon - from.lon
        val dy = to.lat - from.lat

        return Math.sqrt(dx * dx + dy * dy)
    }

    override fun travel(from: Location, toward: Location, dist: Double): Location {
        val pct = dist / dist(from, toward)

        val lat = from.lat + (toward.lat - from.lat) * pct
        val lon = from.lon + (toward.lon - from.lon) * pct
        return Location(lat, lon)
    }
}
