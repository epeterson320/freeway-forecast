package co.ericp.freewayforecast.netdata;

import android.content.Context;

import com.bluesierralabs.freewayforecast.R;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsRoute;

/**
 * Wrapper to Google Directions API
 */
public class Directions {
    static public void get(String from, String to, Context context, final Callbacks callbacks){

        GeoApiContext apiContext =
                new GeoApiContext().setApiKey(context.getString(R.string.maps_api_key));

        DirectionsApi.getDirections(apiContext, from, to)
            .setCallback(new PendingResult.Callback<DirectionsRoute[]>() {
                @Override
                public void onResult(DirectionsRoute[] result) {
                    callbacks.directionsResult(result);
                }

                @Override
                public void onFailure(Throwable e) {
                    callbacks.directionsFail(e);
                }
        });
    }

    public interface Callbacks {
        public void directionsResult(DirectionsRoute[] result);
        public void directionsFail(Throwable e);
    }
}
