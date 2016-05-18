package co.ericp.freewayforecast

import rx.Single

/**
 * A mechanism to get locations.
 */
interface LocationSource {
    fun currentLocation(): Single<List<Location>>
    fun getLocations(query: String): Single<List<Location>>
}
