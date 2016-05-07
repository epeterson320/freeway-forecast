package co.ericp.freewayforecast.netdata;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Class to get the user's current place
 */
public class CurrentLocation {
    public static void get(GoogleApiClient client, final Callbacks callbacks){
        Places.PlaceDetectionApi
              .getCurrentPlace(client, null)
              .setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                Place mostLikelyLoc = null;
                float maxLikelihood = 0;
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    if (placeLikelihood.getLikelihood() > maxLikelihood){
                        maxLikelihood = placeLikelihood.getLikelihood();
                        mostLikelyLoc = placeLikelihood.getPlace();
                    }
                    Log.i("Location", String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
                if (mostLikelyLoc != null) {
                    callbacks.placeResult(mostLikelyLoc);
                } else {
                    Log.i("Location", "No locations found");
                }
            }
        });
    }

    public interface Callbacks{
        public void placeResult(Place place);
    }
}
