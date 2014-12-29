package com.bluesierralabs.freewayforecast.Tasks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.bluesierralabs.freewayforecast.Helpers.DirectionsJSONParser;
import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.RouteSelectActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by timothy on 12/27/14.
 */

/** A class to parse the Google Places in JSON format */
public class FetchRoutesTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
{
    Trip tripInstance = Trip.getInstance();

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
    {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        // Add the starting location marker to the trip
//        tripInstance.addHourMarker(fromPosition);

        try
        {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);

//            hourPoints = parser.getHourPoints();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // Add the destination marker to the list
//        usersTrip.addHourMarker(toPosition);

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
//            Toast.makeText(RouteSelectActivity.this.getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
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

//            Log.e("Trying to add hour markers", "" + hourPoints.size());
        Log.e("Trying to add hour markers", "" + tripInstance.getHourMarkers().size());
//            for (int i = 0; i < hourPoints.size(); i++) {
        for (int i = 0; i < tripInstance.getHourMarkers().size(); i++) {
//                map.addMarker(new MarkerOptions().position(hourPoints.get(i)));
//            map.addMarker(new MarkerOptions().position(tripInstance.getHourMarkers().get(i)));
        }

        // Drawing polyline in the Google Map for the i-th route
//        RouteSelectActivity.this.map.addPolyline(lineOptions);
    }
}

