package co.ericp.freewayforecast

import java.lang.Math.PI;
import java.lang.Math.sin
import java.lang.Math.cos
import java.lang.Math.acos
/**
 * A latitude and longitude.
 */
data class LatLon(val lat: Double, val lon: Double) {
    companion object {
        /** Earth's radius in meters */
        val r = 6371000

        internal fun radians(deg: Double): Double {
            return (deg * PI / 180)
        }

        /**
         * Gets the distance between two locations in meters.
         */
        fun dist(l1: LatLon, l2: LatLon): Double {
            val φ1 = radians(l1.lat)
            val φ2 = radians(l2.lat)
            val Δλ = radians(l2.lon - l1.lon)

            return acos(sin(φ1) * sin(φ2) +
                        cos(φ1) * cos(φ2) * cos(Δλ)) * r
        }
    }
}
