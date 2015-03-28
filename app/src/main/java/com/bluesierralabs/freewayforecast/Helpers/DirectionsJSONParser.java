package com.bluesierralabs.freewayforecast.helpers;


import android.util.Log;

import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.Models.WeatherItem;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by timothy on 11/29/14.
 *
 * Source from http://jigarlikes.wordpress.com/2013/04/26/driving-distance-and-travel-time-
 * duration-between-two-locations-in-google-map-android-api-v2/
 */

public class DirectionsJSONParser
{
    private Trip tripInstance = Trip.getInstance();

    /** Number of seconds in an hour */
    private static int hour = 3600;

    private List<LatLng> hourPoints;

    public List<LatLng> getHourPoints() {
        return hourPoints;
    }

    private LatLng getPointBetween(LatLng start, LatLng end, double percent)
    {
        Log.e("Getting point between locations, percent", "" + percent);

        Double latitude;
        Double longitude;
        Double latDifference;
        Double lngDifference;

        if (start.latitude > end.latitude) {
            latDifference = start.latitude - end.latitude;
            latitude = start.latitude - (latDifference * percent);
        } else {
            latDifference = end.latitude - start.latitude;
            latitude = start.latitude + (latDifference * percent);
        }

        if (start.longitude > end.longitude) {
            lngDifference = start.longitude - end.longitude;
            longitude = start.longitude - (lngDifference * percent);
        } else {
            lngDifference = end.longitude - start.longitude;
            longitude = start.longitude + (lngDifference * percent);
        }

        return new LatLng(latitude, longitude);
    }

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject)
    {
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject jDistance = null;
        JSONObject jDuration = null;
        JSONObject jDurationSeconds = null;

        int routeDuration;
        int newRouteDuration;
        int itemsForRoute;
        boolean usedFirstHour;

        try
        {
            jRoutes = jObject.getJSONArray("routes");

            Log.e("Found routes: ", "" + jRoutes.length());

            Date firstHour = Utilities.toNearestWholeHour(tripInstance.getTripStart());

            /** Traversing all routes */
            for (int routeNum = 0; routeNum < jRoutes.length(); routeNum++)
            {
                // Set the duration of the route to zero seconds
                routeDuration = 0;
                itemsForRoute = 0;
                usedFirstHour = false;

                // Get the summary of the trip from the json data
                String routeSummary = ((JSONObject) jRoutes.get(routeNum)).getString("summary");
                tripInstance.addSummary(routeSummary);
                Log.e("DirectionsJSONParser", "route summary - " + routeSummary);

                jLegs = ((JSONObject) jRoutes.get(routeNum)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs of the route */
                for (int j = 0; j < jLegs.length(); j++)
                {
                    /** Getting distance from the json data */
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    hmDistance.put("distance", jDistance.getString("text"));

                    /** Getting duration from the json data */
                    jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    HashMap<String, String> hmDuration = new HashMap<String, String>();
                    hmDuration.put("duration", jDuration.getString("text"));

                    /** Adding distance object to the path */
                    path.add(hmDistance);

                    /** Adding duration object to the path */
                    path.add(hmDuration);

                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++)
                    {
                        // Always add the origin marker, even if it doesn't fall on a hour
                        if (k == 0) {
                            // Create a JSON object from the step
                            JSONObject step = (JSONObject) jSteps.get(k);

                            // Get the start location of the step
                            Double stepStartLat;
                            stepStartLat = ((JSONObject) step.get("start_location")).getDouble("lat");
                            Double stepStartLng;
                            stepStartLng = ((JSONObject) step.get("start_location")).getDouble("lng");

                            // Get the end location of the step
//                            Double stepEndLat;
//                            stepEndLat = ((JSONObject) step.get("end_location")).getDouble("lat");
//                            Double stepEndLng;
//                            stepEndLng = ((JSONObject) step.get("end_location")).getDouble("lng");

                            LatLng start = new LatLng(stepStartLat, stepStartLng);
//                            LatLng end = new LatLng(stepEndLat, stepEndLng);

                            Log.e("Adding marker", "adding origin marker for route " + routeNum);

                            Log.e("Leaving origin at", "" + tripInstance.getTripStart().toString());

                            // Create a weather item and add it to the trip object
                            WeatherItem tripPoint = new WeatherItem(start);
                            tripPoint.setRouteNumber(routeNum);
                            tripPoint.setDate(tripInstance.getTripStart());
                            tripInstance.addTripWeatherItem(tripPoint);
                        }

                        // Get the polyline information
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = this.decodePoly(polyline);

                        /** Loop through all points of the polyline */
                        for (int l = 0; l < list.size(); l++)
                        {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }

                        // Get the duration of the step (in seconds) and add it to the trip duration
                        int stepDuration = ((JSONObject) ((JSONObject) jSteps.get(k)).get("duration")).getInt("value");

                        // The step duration can do a couple things...
                        // 1 - It could begin a new 'hour' of the trip
                        // 2 - It could finish off an existing 'hour' of the trip
                        // 3 - It could contain several 'hour's of the trip

                        // The goal here is to pin an hour marker at every 3600 second interval of
                        // the trip along with its approximate longitude and latitude
                        newRouteDuration = routeDuration + stepDuration;

                        // Determine the step modulus
                        int stepModulus;
                        int leftToMakeHour;
                        int difference = 0;
                        if (!usedFirstHour) {
                            difference = (int) ((firstHour.getTime() - tripInstance.getTripStart().getTime())) / 1000;
                            Log.e("Difference", "" + difference);
                            if (difference == 0) {
                                stepModulus = newRouteDuration % hour;
                                leftToMakeHour = hour - (routeDuration % hour);
                            } else {
                                stepModulus = newRouteDuration % difference;
//                            stepModulus = newRouteDuration % hour;
                                leftToMakeHour = difference - (routeDuration % difference);
                            }
                        } else {
                            stepModulus = newRouteDuration % hour;
                            leftToMakeHour = hour - (routeDuration % hour);
                        }



                        Log.e("Step duration", "" + stepDuration);
                        Log.e("Step hour modulus", "" + stepModulus);

                        // If the step duration is greater than the step modulus, then the marker goes here.
                        if (stepDuration > stepModulus) {
                            // The marker needs to go the distance from the steps start to end
                            // times the modulus divided by 3600 (seconds in an hour)

                            Log.e("Route duration: " + routeDuration + " and adding step with duration", "" + stepDuration);

                            Log.e("leftToMakeHour", "" + leftToMakeHour);

                            // Create a JSON object from the step
                            JSONObject step = (JSONObject) jSteps.get(k);

                            // Get the start location of the step
                            Double stepStartLat;
                            stepStartLat = ((JSONObject) step.get("start_location")).getDouble("lat");
                            Double stepStartLng;
                            stepStartLng = ((JSONObject) step.get("start_location")).getDouble("lng");

                            // Get the end location of the step
                            Double stepEndLat;
                            stepEndLat = ((JSONObject) step.get("end_location")).getDouble("lat");
                            Double stepEndLng;
                            stepEndLng = ((JSONObject) step.get("end_location")).getDouble("lng");

                            LatLng start = new LatLng(stepStartLat, stepStartLng);
                            LatLng end = new LatLng(stepEndLat, stepEndLng);

//                            Double hourNum = Math.floor((routeDuration + stepDuration) / 3600);
//                            int hour = hourNum.intValue();

                            int tempDuration = stepDuration;
                            int additionalMarkers = 0;
                            int additionalMarkersDuration;
                            LatLng marker;
                            Double percentage;

                            while (tempDuration > 0) {
                                if(additionalMarkers == 0) {
                                    percentage = (double) leftToMakeHour / (double) stepDuration;
                                    marker = getPointBetween(start, end, percentage);
                                    if ((tempDuration - leftToMakeHour) < hour) {
                                        // there is no way there can be an additional marker so go
                                        // ahead and break out of the loop now
                                        tempDuration = 0;
                                    } else {
                                        tempDuration = tempDuration - leftToMakeHour;
                                    }
                                } else {
                                    // The time used up so far by the step.
                                    additionalMarkersDuration = stepDuration - tempDuration + hour;

                                    percentage = (double) additionalMarkersDuration / (double) stepDuration;
                                    marker = getPointBetween(start, end, percentage);

                                    tempDuration = tempDuration - hour;

                                    if (tempDuration < hour) {
                                        // break out of the loop now
                                        tempDuration = 0;
                                    }
//                                    Log.e("Left over", "" + tempDuration);
                                }

                                Log.e(DirectionsJSONParser.class.getName(), "Adding hour marker for route " + routeNum);

                                // Create date for weather item
                                // TODO: The time calculation here is definitely wrong
                                long milliseconds = tripInstance.getTripStart().getTime() + ((routeDuration + stepDuration) * 1000);
                                Date pointTime = new Date(milliseconds);

                                // Create a weather item and add it to the trip object
                                WeatherItem tripPoint = new WeatherItem(marker);
                                tripPoint.setRouteNumber(routeNum);
                                tripPoint.setDate(pointTime);
                                tripInstance.addTripWeatherItem(tripPoint);

//                                long markerTime;
//
//                                if ((!usedFirstHour) && (difference != 0)) {
//                                    markerTime = tripInstance.getTripStart().getTime() + difference;
//                                }

                                Log.e("Route duration at marker point", "" + (routeDuration + leftToMakeHour));

                                // Anytime an point is added, the first hour must have been already used
                                usedFirstHour = true;

                                // Increment the marker counter
                                additionalMarkers++;

                                // Increment the items that are in this route
                                itemsForRoute++;
                            }
                        }

                        // Always add the destination marker, even if it doesn't fall on a hour
                        if (k == (jSteps.length() - 1)) {
                            // Create a JSON object from the step
                            JSONObject step = (JSONObject) jSteps.get(k);

                            // Get the start location of the step
//                            Double stepStartLat;
//                            stepStartLat = ((JSONObject) step.get("start_location")).getDouble("lat");
//                            Double stepStartLng;
//                            stepStartLng = ((JSONObject) step.get("start_location")).getDouble("lng");

                            // Get the end location of the step
                            Double stepEndLat;
                            stepEndLat = ((JSONObject) step.get("end_location")).getDouble("lat");
                            Double stepEndLng;
                            stepEndLng = ((JSONObject) step.get("end_location")).getDouble("lng");

//                            LatLng start = new LatLng(stepStartLat, stepStartLng);
                            LatLng end = new LatLng(stepEndLat, stepEndLng);

                            Log.e("Adding marker", "adding destination marker for route " + routeNum);

                            // Create date for weather item
                            long milliseconds = tripInstance.getTripStart().getTime() + ((routeDuration + stepDuration) * 1000);
                            Date destinationTime = new Date(milliseconds);

                            Log.e("Arriving to destination at", "" + destinationTime.toString());

                            // Create a weather item and add it to the trip object
                            WeatherItem tripPoint = new WeatherItem(end);
                            tripPoint.setRouteNumber(routeNum);
                            tripPoint.setDate(destinationTime);
                            tripInstance.addTripWeatherItem(tripPoint);
                        }

                        // In either case, add the step duration to the route duration
                        // TODO: Remove this once debugging phase is finished
                        routeDuration = routeDuration + stepDuration;
                    }
                }
                Log.e("Route total duration", "" + routeDuration);

                routes.add(path);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
        }

        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded)
    {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len)
        {
            int b, shift = 0, result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((lat / 1E5)), ((lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}


