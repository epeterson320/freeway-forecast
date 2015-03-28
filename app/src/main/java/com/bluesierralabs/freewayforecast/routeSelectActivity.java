package com.bluesierralabs.freewayforecast;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bluesierralabs.freewayforecast.helpers.BusProvider;
import com.bluesierralabs.freewayforecast.helpers.DirectionsJSONParser;
import com.bluesierralabs.freewayforecast.helpers.InternetHelpers;
import com.bluesierralabs.freewayforecast.helpers.Utilities;
import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.Services.RouteAddedEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.otto.Produce;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteSelectActivity extends FragmentActivity {

    public static int ZOOM_PADDING = 50;

    /** Instance of the trip object that is used/modified throughout the application */
    Trip mTrip;

    /** Map object for displaying the trip route options */
    SupportMapFragment mMapFragment;
    GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_select);

        mTrip = Trip.getInstance();

        // Getting reference to SupportMapFragment of the activity_main
        mMapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        mMap = mMapFragment.getMap();

        // Set the trip start and end markers
        mMap.addMarker(new MarkerOptions().position(mTrip.getTripStartCoordinates()).title("Start"));
        mMap.addMarker(new MarkerOptions().position(mTrip.getTripEndCoordinates()).title("End"));

        // Getting URL to the Google Directions API
        String url = Utilities.getDirectionsUrl(mTrip.getTripStartCoordinates(), mTrip.getTripEndCoordinates());

        // Put a testing list item in the fragment
        BusProvider.getInstance().post(produceRouteSummaryEvent());

        // Start downloading json data from Google Directions API
        new DownloadTask().execute(url);
    }

    @Override protected void onResume() {
        super.onResume();

        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override protected void onPause() {
        super.onPause();

        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
    }

    @Produce
    public RouteAddedEvent produceRouteSummaryEvent() {
        // Provide an initial value for location based on the last known position.
        return new RouteAddedEvent("testing");
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
//            WeatherItem tripStart = new WeatherItem(mTrip.getTripStartCoordinates());
//            mTrip.addTripWeatherItem(tripStart);

            try
            {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            // Add the destination marker to the list
//            WeatherItem tripEnd = new WeatherItem(mTrip.getTripEndCoordinates());
//            mTrip.addTripWeatherItem(tripEnd);

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
                Log.e("RouteSelectActivity", "printing route " + (i + 1));

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
                lineOptions.width(7);

                if (i == 0) {
                    lineOptions.color(Color.parseColor("#009933")); // Contrast green
                } else if (i == 1) {
                    lineOptions.color(Color.BLUE);
                } else if (i == 2) {
                    lineOptions.color(Color.RED);
                } else {
                    lineOptions.color(Color.parseColor("#8000FF")); // Purple
                }

                // Drawing polyline in the Google Map for the i-th route
                RouteSelectActivity.this.mMap.addPolyline(lineOptions);
            }

            // Add the markers to the map
            // TODO: Add this back in later when I return to the marker accuracy
//            for (int i = 0; i < mTrip.getWeatherItems().size(); i++) {
//                mMap.addMarker(new MarkerOptions().position(mTrip.getWeatherItems().get(i).getLocation()));
//            }

            // TODO: Might be able to get away with setting the bounds using only the start and end locations.
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(mTrip.getTripStartCoordinates());
            builder.include(mTrip.getTripEndCoordinates());
            LatLngBounds mapBounds = builder.build();

            // Zoom the mMap to show only the route
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, ZOOM_PADDING));

            // Disable zooming once the trip routes are correctly bounded
            // TODO: Determine if there should be some ability to zoom/pan
            mMap.getUiSettings().setAllGesturesEnabled(false);
        }
    }
}