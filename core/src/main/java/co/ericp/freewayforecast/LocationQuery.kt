package co.ericp.freewayforecast

sealed class LocationQuery {
    class ByName(val name: String) : LocationQuery()
    class ByCoords(val lat: Double, val lon: Double) : LocationQuery()
}

