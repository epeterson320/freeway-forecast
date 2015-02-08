package com.bluesierralabs.freewayforecast;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bluesierralabs.freewayforecast.Helpers.App;
import com.bluesierralabs.freewayforecast.Helpers.DirectionsJSONParser;
import com.bluesierralabs.freewayforecast.Helpers.InternetHelpers;
import com.bluesierralabs.freewayforecast.Helpers.OpenWeatherParser;
import com.bluesierralabs.freewayforecast.Helpers.Utilities;
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

    /** Adapter for the weather object */
    private WeatherAdapter adapter;

    /** Array list for the json results from the weather api calls */
    private ArrayList<String> jsonResults = new ArrayList<String>();

    /** Array list for the weather items */
    private ArrayList<WeatherItem> weatherResults = new ArrayList<WeatherItem>();

    private int pointsToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_forecast);

        // Clear out the array of weather and json results
        jsonResults.clear();

        // Setup the adapter for the weather items
        adapter = new WeatherAdapter(this, R.layout.weather_item, tripInstance.getWeatherItems());

        // Setup the handle for the list view object
        weatherListing = (ListView)findViewById(R.id.listview);

        Log.e("Entering forecast with", "" + tripInstance.getWeatherItems().size() + " items");

        // Get the latitude/longitude markers from the trip instance and work through them
        for (int i=0; i < tripInstance.getWeatherItems().size(); i++) {
            WeatherItem item = tripInstance.getWeatherItem(i);

            if ((i > 0) && (i < (tripInstance.getWeatherItems().size() - 1))) {
                if (item.getRouteNumber() != 0) {
                    tripInstance.removeTripWeatherItem(i);
                } else {
                    // Create the url to get the weather information
                    String url = Utilities.getOpenWeatherMapUrl(item.getLocation());

                    // Create the download task with context
                    DownloadTask downloadTask = new DownloadTask(this);

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            } else {
                // Create the url to get the weather information
                String url = Utilities.getOpenWeatherMapUrl(item.getLocation());

                // Create the download task with context
                DownloadTask downloadTask = new DownloadTask(this);

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String>
    {
        // Context for the Download task
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
                publishProgress(jsonResults.size());

            } catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Runs on the UI thread after publishProgress(Progress...) is invoked.
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            // While the down load tasks are happening, I want to change the background and
            TextView test = (TextView) findViewById(R.id.progress);
            test.setText("Download progress: " + values[0].toString());
        }

        // Executes in UI thread, after the execution of doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            // Add the json data to the results array
            jsonResults.add(result);

            // TODO:
            // This is probably not incredibly efficient but I was getting here with other route's
            // items in the trip instance still so needed to do a double clear. Maybe I can do this
            // better
            for (int i=0; i < tripInstance.getWeatherItems().size(); i++) {
                WeatherItem test = tripInstance.getWeatherItem(i);
                if (test.getRouteNumber() != 0) {
                    tripInstance.removeTripWeatherItem(i);
                }
            }

            Log.e("jsonResults: " + jsonResults.size(), "totalHourMarkers: " + tripInstance.getWeatherItems().size());
            if (jsonResults.size() >= tripInstance.getWeatherItems().size()) {

                Log.e("Download Task", "getting ready to start parsing. Items in trip=" + tripInstance.getWeatherItems().size());

                // Create an instance of the parser task to operate on the json data objects received
                ParserTask parserTask = new ParserTask();

                // Parse all the results when they are collectively ready
                parserTask.execute(jsonResults);
            }
        }
    }

    private class ParserTask extends AsyncTask<ArrayList<String>, Integer, WeatherItem>
    {
        // Parsing the data in non-ui thread
        @Override
        protected WeatherItem doInBackground(ArrayList<String>... jsonData) {
            JSONObject jObject;

            // Before adding the the weather items, clear the holder out
            weatherResults.clear();

            // Create a parser object to parse the json returned from the API call
            OpenWeatherParser parser = new OpenWeatherParser();

            try {
                // parse all the json data returned for the hour locations
                for (int i = 0; i < jsonData[0].size(); i++) {
                    jObject = new JSONObject(jsonData[0].get(i));

//                    Log.e("ParserTask", "Adding a weather item");
                    weatherResults.add(parser.parse(jObject));
//                    publishProgress(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        // Runs on the UI thread after publishProgress(Progress...) is invoked.
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//
//            TextView test = (TextView) findViewById(R.id.progress);
//            test.setText("Progress: " + values.toString());
//        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(WeatherItem result) {

            // Add all the results to the listing
            for (int i=0; i<weatherResults.size(); i++) {
                // Also add the weather item to the trip instance
                WeatherItem currentItem = weatherResults.get(i);

                tripInstance.getWeatherItems().get(i).addWeatherInfo(currentItem.getIcon(),
                        currentItem.getMinTemp(), currentItem.getMaxTemp(),
                        currentItem.getTempAsDouble(), currentItem.getTitle(), currentItem.getDetail());
            }

            // Update the adapter with the updated hour listing
            adapter.setData(tripInstance.getWeatherItems());

            // Setup the weather listing with its adapter
            weatherListing.setAdapter(adapter);
        }
    }
}
