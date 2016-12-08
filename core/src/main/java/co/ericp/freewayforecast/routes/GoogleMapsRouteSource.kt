package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.LocationQuery
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsLeg
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.LatLng
import org.joda.time.Instant
import rx.Observable

/**
 * The one and likely only production implementation of RouteSource.
 *
 * This is backed by the Google Directions API.
 */
class GoogleMapsRouteSource : RouteSource {
    val apiContext = GeoApiContext()

    init {
        apiContext.setApiKey(TODO())
    }

    override fun getRoutes(origin: LocationQuery,
                           destination: LocationQuery,
                           departureTime: Long?): Observable<Route> {

        val request = DirectionsApi.newRequest(apiContext)

        when (origin) {
            is LocationQuery.ByName -> request.origin(origin.name)
            is LocationQuery.ByCoords -> request.origin(LatLng(origin.lat, origin.lon))
        }
        when (destination) {
            is LocationQuery.ByName -> request.destination(destination.name)
            is LocationQuery.ByCoords -> request.destination(LatLng(destination.lat, destination.lon))
        }
        if (departureTime != null) {
            request.departureTime(Instant(departureTime))
        }

        return Observable.error<DirectionsResult>(NotImplementedError())
                .flatMap { Observable.from(it.routes) }
                .flatMap { route -> Observable.just(googleRouteToFFRoute(route)) }
    }

    fun googleRouteToFFRoute(gRoute: DirectionsRoute): Route {
        TODO()
    }

    fun googleLegToFFLeg(gLeg: DirectionsLeg): Leg {
        TODO()
    }

}