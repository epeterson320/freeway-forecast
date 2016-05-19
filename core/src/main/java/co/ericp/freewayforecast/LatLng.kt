package co.ericp.freewayforecast

import java.lang.Math.PI;
import java.lang.Math.sin
import java.lang.Math.cos
import java.lang.Math.acos
/**
 * A latitude and longitude.
 */
data class LatLng(val lat: Double, val lng: Double) {

    companion object Utils {
        val r = 6371000 // Earth's radius in meters

        internal fun radians(deg: Double): Double {
            return (deg * PI / 180)
        }

        /**
         * Gets the distance between two locations in meters.
         */
        fun dist(l1: LatLng, l2: LatLng): Double {
            val φ1 = radians(l1.lat)
            val φ2 = radians(l2.lat)
            val Δλ = radians(l2.lng - l1.lng)

            return acos(sin(φ1) * sin(φ2) +
                        cos(φ1) * cos(φ2) * cos(Δλ)) * r
        }
    }
}
