package co.ericp.freewayforecast

import rx.Single

/**
 * A mechanism to get locations.
 */
interface LocationSource {
    fun currentLocation(): Single<List<Location>>
    fun getLocation(query: String): Single<List<Location>>
}
