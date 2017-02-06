package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.routeForecast.Location
import co.ericp.freewayforecast.routeForecast.LocationQuery
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.*
import io.reactivex.Observable
import org.joda.time.Instant
import kotlin.collections.sumBy

/**
 * The one and likely only production implementation of RouteSource.
 *
 * This is backed by the Google Directions API.
 */
class GoogleMapsRouteSource(apiKey: String) : RouteSource {
    val apiContext: GeoApiContext = GeoApiContext().setApiKey(apiKey)

    override fun getRoutes(origin: LocationQuery,
                           destination: LocationQuery,
                           departureTime: Long): Observable<Route> {

        val request = DirectionsApi.newRequest(apiContext)

        when (origin) {
            is LocationQuery.ByName -> request.origin(origin.name)
            is LocationQuery.ByCoords -> request.origin(LatLng(origin.lat, origin.lon))
        }
        when (destination) {
            is LocationQuery.ByName -> request.destination(destination.name)
            is LocationQuery.ByCoords -> request.destination(LatLng(destination.lat, destination.lon))
        }
        request.departureTime(Instant(departureTime))

        val responseRx = Observable.create<DirectionsResult> { emitter ->

            val callback = object : PendingResult.Callback<DirectionsResult> {

                override fun onResult(result: DirectionsResult) {
                    emitter.onNext(result)
                    emitter.onComplete()
                }

                override fun onFailure(e: Throwable?) {
                    emitter.onError(e)
                }
            }

            emitter.setCancellable { request.cancel() }

            request.setCallback(callback)
        }

        return responseRx
                .flatMap { Observable.fromIterable(it.routes.toList()) }
                .flatMap { route -> Observable.just(googleRouteToFFRoute(route, departureTime)) }
    }

    fun googleRouteToFFRoute(gRoute: DirectionsRoute, departureTime: Long): Route {

        val legs = gRoute.legs.map { googleLegToFFLeg(it) }
        val firstLeg = legs.first()
        val lastLeg = legs.last()

        val durationMillis = gRoute.legs.sumBy { it.duration.inSeconds.toInt() }.toLong() * 1000;
        val arrivalTime = departureTime + durationMillis

        return Route(
                gRoute.summary,
                legs.map(Leg::distance).sum(),
                legs.map(Leg::duration).sum(),
                departureTime,
                arrivalTime,
                location(gRoute.bounds.northeast),
                location(gRoute.bounds.southwest),
                firstLeg.start,
                lastLeg.end,
                gRoute.legs.map { googleLegToFFLeg(it) }
        )
    }

    fun googleLegToFFLeg(gLeg: DirectionsLeg): Leg {
        return Leg(
                location(gLeg.startLocation, gLeg.startAddress),
                location(gLeg.endLocation, gLeg.endAddress),
                gLeg.distance.inMeters.toDouble(),
                gLeg.duration.inSeconds * 1000,
                gLeg.steps.map { googleStepToFFStep(it) }
        )
    }

    fun googleStepToFFStep(gStep: DirectionsStep): Step {
        val polyline = gStep.polyline.decodePath().map { location(it) }
        return Step(
                gStep.htmlInstructions,
                gStep.distance.inMeters.toDouble(),
                gStep.duration.inSeconds * 1000,
                location(gStep.startLocation),
                location(gStep.endLocation),
                polyline
        )
    }

    fun location(latLng: LatLng, name: String? = null): Location
            = Location(latLng.lat, latLng.lng, name)
}