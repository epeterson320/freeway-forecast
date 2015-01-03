package com.bluesierralabs.freewayforecast;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.bluesierralabs.freewayforecast.Helpers.App;
import com.bluesierralabs.freewayforecast.Helpers.DirectionsJSONParser;
import com.bluesierralabs.freewayforecast.Helpers.InternetHelpers;
import com.bluesierralabs.freewayforecast.Helpers.OpenWeatherParser;
import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.Models.WeatherItem;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Trip forecast
 */
public class TripForecastActivity extends Activity {

    /** Instance of the trip object that is used/modified throughout the application */
    private Trip tripInstance = Trip.getInstance();

    /** List view to populate with forecast items */
    private ListView weatherListing;

    // Create an array list of weather items to set with trip forecast information
    ArrayList<WeatherItem> hourInfoList = new ArrayList<WeatherItem>();

    WeatherAdapter adapter;

    private ArrayList<String> jsonResults = new ArrayList<String>();

    private ArrayList<WeatherItem> weatherResults = new ArrayList<WeatherItem>();

    private int totalHourMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_forecast);

        // Clear out the array of weather and json results
        weatherResults.clear();
        jsonResults.clear();

        // Setup the adapter for the weather items
        adapter = new WeatherAdapter(this, R.layout.weather_item, hourInfoList);

        // Setup the handle for the list view object
        weatherListing = (ListView)findViewById(R.id.listview);

        // Get the latitude/longitude markers from the trip instance and work through them
        List<LatLng> markers = tripInstance.getHourMarkers();
        totalHourMarkers = markers.size();

        for (int i=0; i < markers.size(); i++) {
            // Create the url to get the weather information
            String url = getForecastUrl(markers.get(i));

            // Create the download task with context
            DownloadTask downloadTask = new DownloadTask(this);

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        private Context mContext;

        public DownloadTask (Context context){
            mContext = context;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {
            // For storing data from web service
            String data = "";

            try
            {
                // Fetching the data from web service
//                Log.e("DownloadTask", "downloading weather data");
                data = InternetHelpers.downloadUrl(url[0]);
            } catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            // Add the json data to the results array
            jsonResults.add(result);

            Log.e("jsonResults: " + jsonResults.size(), "totalHourMarkers: " + totalHourMarkers);
            if (jsonResults.size() >= totalHourMarkers) {
                // Create an instance of the parser task to operate on the json data objects received
                ParserTask parserTask = new ParserTask(mContext);

                parserTask.execute(jsonResults);
            }

//            // Create an instance of the parser task to operate on the json data received.
//            ParserTask parserTask = new ParserTask(mContext);
//
//            // Invokes the thread for parsing the JSON data
//            parserTask.execute(result);
        }
    }

//    private class ParserTask extends AsyncTask<String, Integer, WeatherItem> {
    private class ParserTask extends AsyncTask<ArrayList<String>, Integer, WeatherItem> {
        private Context mContext;

        public ParserTask (Context context){
            mContext = context;
        }

        // Parsing the data in non-ui thread
        @Override
//        protected WeatherItem doInBackground(String... jsonData) {
        protected WeatherItem doInBackground(ArrayList<String>... jsonData) {
            JSONObject jObject;

            try {
                for (int i = 0; i < jsonData[0].size(); i++) {
                    jObject = new JSONObject(jsonData[0].get(i));

                    OpenWeatherParser parser = new OpenWeatherParser();

//                    Log.e("ParserTask", "Adding a weather item");
                    weatherResults.add(parser.parse(jObject));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            try {
//                jObject = new JSONObject(jsonData[0]);
//                OpenWeatherParser parser = new OpenWeatherParser();
//
//                // Starts parsing data
//                parser.parse(jObject);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            return null;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(WeatherItem result) {

            for (int i=0; i<weatherResults.size(); i++) {
                hourInfoList.add(weatherResults.get(i));
            }

//            hourInfoList.add(result);

            // Update the adapter with the updated hour listing
            adapter.setData(hourInfoList);

            weatherListing.setAdapter(adapter);
        }
    }

    private String getForecastUrl(LatLng location)
    {
        // Origin of route
        String point = "lat=" + location.latitude + "&lon=" + location.longitude;

        // Building the url to the weather api
        String url = "http://api.openweathermap.org/data/2.5/weather?" + point;

        Log.e("TripForecastActivity.getForecastUrl", url);

        return url;
    }
}
