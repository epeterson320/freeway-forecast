package co.ericp.freewayforecast

/**
 * Created by eric on 12/7/16.
 */
interface DistanceCalculator {
    fun travel(from: Location, toward: Location, dist: Double): Location
    fun dist(from: Location, to: Location): Double
}