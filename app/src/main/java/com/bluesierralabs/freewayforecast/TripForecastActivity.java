package com.bluesierralabs.freewayforecast;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_forecast);

        // Create an array list of weather items to set with trip forecast information
        ArrayList<WeatherItem> hourInfoList = new ArrayList<WeatherItem>();

        // Get the latitude/longitude markers from the trip instance and work through them
        List<LatLng> markers = tripInstance.getHourMarkers();
        for (int i=0; i < markers.size(); i++) {
            // Create the url to get the weather information
            String url = getForecastUrl(markers.get(i));

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            WeatherItem newMarker = new WeatherItem(R.drawable.clouds, "test", markers.get(i).toString(), 10);
            hourInfoList.add(newMarker);
        }

        // Set the forecast items to the list view
        WeatherAdapter adapter = new WeatherAdapter(this, R.layout.weather_item, hourInfoList);
        weatherListing = (ListView)findViewById(R.id.listview);
        weatherListing.setAdapter(adapter);
    }

    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {
            // For storing data from web service
            String data = "";

            try
            {
                // Fetching the data from web service
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

            // Create an instance of the parser task to operate on the json data received.
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;

            try {
                jObject = new JSONObject(jsonData[0]);
                OpenWeatherParser parser = new OpenWeatherParser();

                // Starts parsing data
                parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//            Log.e(TripForecastActivity.class.getName(), "done parsing url");
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
