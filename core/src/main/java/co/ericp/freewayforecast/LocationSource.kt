package co.ericp.freewayforecast

import io.reactivex.Observable

/**
 * A mechanism to get locations.
 */
interface LocationSource {
    fun currentLocation(): Observable<Location>
    fun getLocations(query: String): Observable<Location>
}
