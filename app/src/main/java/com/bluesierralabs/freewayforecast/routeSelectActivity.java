package com.bluesierralabs.freewayforecast;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bluesierralabs.freewayforecast.Helpers.DirectionsJSONParser;
import com.bluesierralabs.freewayforecast.Helpers.InternetHelpers;
import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.Models.WeatherItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RouteSelectActivity extends FragmentActivity {

    /** Instance of the trip object that is used/modified throughout the application */
    Trip tripInstance = Trip.getInstance();

    /** Map object for displaying the trip route options */
    GoogleMap map;

    List<LatLng> hourPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_select);

        // Initializing

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        this.map = fm.getMap();

        // Set the trip start and end markers
        map.addMarker(new MarkerOptions().position(tripInstance.getTripStartCoordinates()).title("Start"));
        map.addMarker(new MarkerOptions().position(tripInstance.getTripEndCoordinates()).title("End"));

        // Getting URL to the Google Directions API
        String url = RouteSelectActivity.this.getDirectionsUrl(tripInstance.getTripStartCoordinates(), tripInstance.getTripEndCoordinates());

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Compose the request url string to get the directions between two longitude and latitude
     * points
     *
     * @param origin LatLng object of the trip's starting location
     * @param destination LatLng object of the trip's ending loction
     * @return String of the url to download
     */
    private String getDirectionsUrl(LatLng origin, LatLng destination)
    {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /** Fetches data from url passed which then calls the ParserTask */
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
//                data = RouteSelectActivity.this.downloadUrl(url[0]);
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

    /** Respond to the "Select route" button being pressed */
    public void submitRoute(View view) {

        // Go the the trip forecast activity
        Intent choseRoute = new Intent(this, TripForecastActivity.class);
        startActivity(choseRoute);
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
        {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            // Add the starting location marker to the trip
            tripInstance.addHourMarker(tripInstance.getTripStartCoordinates());

            // TODO: Instead of using hour markers, start using the weatherItems from the trip instance
            WeatherItem tripStart = new WeatherItem(tripInstance.getTripStartCoordinates());
            // TODO: Want to start this
//            tripInstance.addTripWeatherItem(tripStart);

            try
            {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);

                hourPoints = parser.getHourPoints();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            // TODO: And this too
            WeatherItem tripEnd = new WeatherItem(tripInstance.getTripEndCoordinates());
//            tripInstance.addTripWeatherItem(tripEnd);

            // Add the destination marker to the list
            tripInstance.addHourMarker(tripInstance.getTripEndCoordinates());

            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1)
            {
                Toast.makeText(RouteSelectActivity.this.getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++)
            {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0)
                    { // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1)
                    { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }

            Log.e("Trying to add hour markers", "" + hourPoints.size());
//            Log.e("Trying to add hour markers", "" + usersTrip.getHourMarkers().size());
            for (int i = 0; i < hourPoints.size(); i++) {
//            for (int i = 0; i < usersTrip.getHourMarkers().size(); i++) {
                map.addMarker(new MarkerOptions().position(hourPoints.get(i)));
//                map.addMarker(new MarkerOptions().position(usersTrip.getHourMarkers().get(i)));
            }

            // Drawing polyline in the Google Map for the i-th route
            RouteSelectActivity.this.map.addPolyline(lineOptions);
        }
    }
}