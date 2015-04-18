package com.bluesierralabs.freewayforecast;

import android.os.AsyncTask;
import android.util.Log;

import com.bluesierralabs.freewayforecast.models.WeatherItem;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Asynchronously gets weather for the route from weather APIs
 */
public class TripWeatherTask extends AsyncTask<Void, Integer, List<WeatherItem>> {
    protected TripWeatherCallbacks mCallback;
    protected Calendar mDepartingOn;
    protected DirectionsRoute mRoute;

    public TripWeatherTask(DirectionsRoute route,
                           Calendar departingOn,
                           TripWeatherCallbacks callback){
        mCallback = callback;
        mCallback.onTripWeatherComplete(new ArrayList<WeatherItem>());
    }

    protected List<WeatherItem> doInBackground(Void... params){

        List<WeatherItem> result = new ArrayList<>();

        try {
            Thread.sleep(800);
            publishProgress(2000);
            Thread.sleep(800);
            publishProgress(4000);
            Thread.sleep(800);
            publishProgress(8000);
            Thread.sleep(800);

            result.add(new WeatherItem("I-95", "sunny", "75 F", "11:00 AM"));
            result.add(new WeatherItem("I-95", "sunny", "79 F", "12:00 PM"));
            result.add(new WeatherItem("US 430 N", "sunny", "76 F", "1:00 PM"));
            result.add(new WeatherItem("Lee Highway", "cloudy", "71 F", "1:43 PM"));

        } catch (java.lang.InterruptedException e) {
            Log.i("WeatherTask", "Thread Interrupted");
        }
        return result;
    }

    protected void onProgressUpdate(Integer... progress){
        mCallback.onTripWeatherProgress(progress[0]);
    }

    protected void onPostExecute(List<WeatherItem> result){
        mCallback.onTripWeatherComplete(result);
    }

    public interface TripWeatherCallbacks{
        public void onTripWeatherComplete(List<WeatherItem> tripWeather);
        public void onTripWeatherProgress(Integer progress);
    }
}
