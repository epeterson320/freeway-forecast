package co.ericp.freewayforecast

/**
 * A point on the globe. Contains a latitude, longitude, and optional name.
 */
data class Location(
        val lat: Double,
        val lon: Double,
        val name: String? = null
) {
    constructor(lat: Number, lon: Number, name: String? = null)
            : this(lat.toDouble(), lon.toDouble(), name)

    override fun toString(): String =
        if (name == null) "$lat, $lon" else "$lat, $lon ($name)"
}
