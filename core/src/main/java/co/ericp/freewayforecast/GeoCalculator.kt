package co.ericp.freewayforecast

import java.lang.Math.acos
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.PI

/**
 * Calculator functions for geographic measurements.
 *
 * The functions are backed by simple math calculations, but the
 * implementations could be swapped out for something more robust if needed,
 * e.g. https://github.com/mgavaghan/geodesy
 */
object GeoCalculator : DistanceCalculator {
    internal val r: Double = 6371000.0 // Earth's radius in meters

    internal fun radians(deg: Double): Double {
        return (deg * PI / 180)
    }

    /**
     * Get the distance between two locations in meters.
     *
     * Uses the Haversine formula.
     * https://en.wikipedia.org/wiki/Great-circle_distance#Computational_formulas
     */
    override fun dist(from: Location, to: Location): Double {
        val φ1 = radians(from.lat)
        val φ2 = radians(to.lat)
        val Δλ = radians(to.lon - from.lon)

        // Get distance in radians
        val rDist: Double = acos(
                sin(φ1) * sin(φ2) +
                        cos(φ1) * cos(φ2) * cos(Δλ)
        )

        // Convert from radians to meters
        return rDist * r
    }

    /**
     * Given two locations, calculates a location between them a given distance
     * from the first.
     *
     * Interpolates like it's cartesian coordinates.
     *
     * @param from The start location
     * @param toward A location in the direction travelling.
     * @param dist The distance travelled, in meters.
     * @return The computed location
     */
    override fun travel(from: Location, toward: Location, dist: Double): Location {
        val fullDist = GeoCalculator.dist(from, toward)
        val pct = dist / fullDist
        val lat = from.lat + (toward.lat - from.lat) * pct
        val lon = from.lon + (toward.lon - from.lon) * pct
        return Location(lat, lon)
    }
}