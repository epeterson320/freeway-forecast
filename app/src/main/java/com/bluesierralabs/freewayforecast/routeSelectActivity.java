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
import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.Tasks.FetchRoutesTask;
import com.bluesierralabs.freewayforecast.Tasks.FetchUrl;
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
    Trip tripInstance = Trip.getInstance();

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    List<LatLng> hourPoints;

//    LatLng fromPosition = new LatLng(42.3584865, -71.05985749999999);   // Boston
//    LatLng toPosition = new LatLng(37.7833, -122.4167); // San Francisco

    LatLng fromPosition = tripInstance.getTripStartCoordinates();
    LatLng toPosition = tripInstance.getTripEndCoordinates();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_select);

        // Initializing
        this.markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        this.map = fm.getMap();

        // Set the trip start and end markers
        map.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
        map.addMarker(new MarkerOptions().position(toPosition).title("End"));

        // Getting URL to the Google Directions API
        String url = RouteSelectActivity.this.getDirectionsUrl(fromPosition, toPosition);

        // Start downloading json data from Google Directions API
        FetchUrl directionsJson = new FetchUrl();
        directionsJson.execute(url);
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

    /* Listener for button press to submit route to be used during trip */
    public void submitRoute(View view) {

        // Go the the trip forecast activity
        Intent choseRoute = new Intent(this, TripForecastActivity.class);
        startActivity(choseRoute);
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
}
