package co.ericp.freewayforecast

sealed class LocationQuery {
    class ByName(val name: String) : LocationQuery()
    class ByCoords(val coords: Location) : LocationQuery()
}

