package co.ericp.freewayforecast.routeForecast

/**
 * Functions for calculating geographic distances.
 */
interface DistanceCalculator {
    fun travel(from: Location, toward: Location, dist: Double): Location
    fun dist(from: Location, to: Location): Double
}