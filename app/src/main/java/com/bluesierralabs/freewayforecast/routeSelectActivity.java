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
import android.widget.TextView;
import android.widget.Toast;

import com.bluesierralabs.freewayforecast.Helpers.DirectionsJSONParser;
import com.bluesierralabs.freewayforecast.Helpers.GMapV2Direction;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    GoogleMap mMap;
    GMapV2Direction md;

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    List<LatLng> hourPoints;
    TextView tvDistanceDuration;

    LatLng fromPosition = new LatLng(42.3584865, -71.05985749999999);
    LatLng toPosition = new LatLng(33.7488397, -84.39293219999999);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_select);

        //this.tvDistanceDuration = (TextView) this.findViewById(R.id.tv_distance_time);
        // Initializing
        this.markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        this.map = fm.getMap();

        // Set the trip start and end markers
        map.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
        map.addMarker(new MarkerOptions().position(toPosition).title("End"));

        // Enable MyLocation Button in the Map
//        this.map.setMyLocationEnabled(true);

//        // Adding new item to the ArrayList
//        RouteSelectActivity.this.markerPoints.add(fromPosition);
//
//        RouteSelectActivity.this.markerPoints.add(toPosition);
//
//        // Creating MarkerOptions
//        MarkerOptions options = new MarkerOptions();
//
//        // Setting the position of the marker
////        options.position(point);
//
//        /**
//         * For the start location, the color of marker is GREEN and
//         * for the end location, the color of marker is RED.
//         */
//        if (RouteSelectActivity.this.markerPoints.size() == 1)
//        {
//            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        } else if (RouteSelectActivity.this.markerPoints.size() == 2)
//        {
//            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        }
//
//        // Add new marker to the Google Map Android API V2
//        RouteSelectActivity.this.map.addMarker(options);
//
//        // Checks, whether start and end locations are captured
//        if (RouteSelectActivity.this.markerPoints.size() >= 2)
//        {
            LatLng origin = fromPosition;   // RouteSelectActivity.this.markerPoints.get(0);
            LatLng dest = toPosition; //RouteSelectActivity.this.markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = RouteSelectActivity.this.getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
//        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest)
    {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

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

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e)
        {
            Log.d("Exception while downloading url", e.toString());
        } finally
        {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
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
                data = RouteSelectActivity.this.downloadUrl(url[0]);
            } catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

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

            //RouteSelectActivity.this.tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);

            Log.e("Trying to add hour markers", "" + hourPoints.size());
            for (int i = 0; i < hourPoints.size(); i++) {
                map.addMarker(new MarkerOptions().position(hourPoints.get(i)));
            }

            // Drawing polyline in the Google Map for the i-th route
            RouteSelectActivity.this.map.addPolyline(lineOptions);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void submitRoute(View view) {

        // Go the the trip forecast activity
        Intent choseRoute = new Intent(this, TripForecastActivity.class);
        startActivity(choseRoute);
    }
}
