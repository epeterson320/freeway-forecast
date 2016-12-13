package co.ericp.freewayforecast

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.PlaceDetectionApi
import com.google.android.gms.location.places.Places
import io.reactivex.Observable

/**
 * Resource to get the user's location via Google Play Services.
 */
class PlayServicesLocationSource(val client: GoogleApiClient) : LocationSource {

    val curLocApi: FusedLocationProviderApi = LocationServices.FusedLocationApi
    val curPlaceApi: PlaceDetectionApi = Places.PlaceDetectionApi

    override fun currentLocation(): Observable<Location> {
        return Observable.create<Location> { emitter ->

            curPlaceApi.getCurrentPlace(client, null).setResultCallback { result ->
                if (result.status.isSuccess) {
                    val mostLikely = result.reduce { p1, p2 ->
                        if (p1.likelihood > p2.likelihood) p1 else p2
                    }
                    val place = mostLikely.place
                    val loc = Location(place.latLng.latitude, place.latLng.longitude, place.name)
                    emitter.onNext(loc)
                    emitter.onComplete()

                } else {
                    emitter.onError(Error(result.status.statusMessage))
                }
                result.release()
            }
        }
    }

    override fun getLocations(query: String): Observable<Location> {
        // This won't be used by the app right now.
        // val locQueryApi: GeoDataApi = Places.GeoDataApi
        TODO()
    }
}