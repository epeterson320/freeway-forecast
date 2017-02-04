package co.ericp.freewayforecast

/**
 * A point on the globe. Contains a latitude, longitude, and optional name.
 */
data class Location(
        val lat: Double,
        val lon: Double,
        val name: String? = null
) {
    constructor(lat: Number, lon: Number, name: CharSequence? = null)
            : this(lat.toDouble(), lon.toDouble(), name?.toString())
}
