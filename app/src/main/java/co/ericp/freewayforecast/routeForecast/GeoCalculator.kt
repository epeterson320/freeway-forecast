package co.ericp.freewayforecast.routeForecast

import org.gavaghan.geodesy.GeodeticCalculator
import org.gavaghan.geodesy.Ellipsoid
import org.gavaghan.geodesy.GlobalCoordinates

/**
 * Calculator functions for geographic measurements.
 */
object GeoCalculator : DistanceCalculator {
    internal val calc = GeodeticCalculator()
    internal val earthModel = Ellipsoid.WGS84

    /**
     * Get the distance between two locations in meters.
     *
     * Uses the Haversine formula.
     * https://en.wikipedia.org/wiki/Great-circle_distance#Computational_formulas
     */
    override fun dist(from: Location, to: Location): Double {
        val fromCoords = GlobalCoordinates(from.lat, from.lon)
        val toCoords = GlobalCoordinates(to.lat, to.lon)
        val curve = calc.calculateGeodeticCurve(earthModel, fromCoords, toCoords)
        return curve.ellipsoidalDistance
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
        val fromCoords = GlobalCoordinates(from.lat, from.lon)
        val towardCoords = GlobalCoordinates(toward.lat, toward.lon)

        val curve = calc.calculateGeodeticCurve(earthModel, fromCoords, towardCoords)
        val azimuth = curve.azimuth
        val end = calc.calculateEndingGlobalCoordinates(earthModel, fromCoords, azimuth, dist)

        return Location(end.latitude, end.longitude)
    }
}