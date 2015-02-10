package com.bluesierralabs.freewayforecast.Helpers;

import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by timothy on 1/7/15.
 *
 * A class to hold generic functions
 */
public class Utilities {
    /**
     * Get a HH:MM AM/PM formatted string from a date object. Returns a formatted string based on
     * the devices time preferences.
     *
     * @param date
     * @return
     */
    public static String getTimeStringFromDate(Date date) {
        // First convert the date object to a calendar object
        if (date == null) {
            Log.e("Utilities.getTimeStringFromDate", "Date object is null");
            return "";
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // Get the hour and minute values as integers
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String readableTime = "";

        String minuteString = "" + minute;
        if (minute < 10) {
            minuteString = "0" + minuteString;
        }

        // Get the date format from the device preferences
        if(DateFormat.is24HourFormat(App.getContext())) {
            if (hour >= 12) {
                readableTime = hour + ":" + minuteString + " PM";
            } else {
                readableTime = hour + ":" + minuteString + " AM";
            }
        } else {
            readableTime = hour + ":" + minuteString;
        }

        return readableTime;
    }

    /**
     * Round a double to a specified number of decimal places. Based on answer on stackoverflow
     * http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
     *
     * @param value
     * @param places
     * @return
     */
    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Create a url string to request the weather data of a longitude and latitude point from the
     * Open Weather Map API service
     *
     * @param location
     * @return
     */
    public static String getOpenWeatherMapUrl(LatLng location)
    {
        // Origin of route
        String point = "lat=" + location.latitude + "&lon=" + location.longitude;

        // Building the url to the weather api
        String url = "http://api.openweathermap.org/data/2.5/weather?" + point;

//        Log.e("TripForecastActivity.getForecastUrl", url);

        return url;
    }

    public static String getForecastIoUrl(LatLng location) {

        // Latitude and longitude section of the url
        String point = location.latitude + "," + location.longitude;

        // API Key
        String apikey = "afa12fa27091cbeef16585dd3bc7b2cc";

        String url = "https://api.forecast.io/forecast/" + apikey + "/" + point;

        return url;
    }

    /**
     * Compose the request url string to get the directions between two longitude and latitude
     * points
     *
     * @param origin LatLng object of the trip's starting location
     * @param destination LatLng object of the trip's ending loction
     * @return String of the url to download
     */
    public static String getDirectionsUrl(LatLng origin, LatLng destination)
    {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Alternatives enabled
        String alternatives = "alternatives=true";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + alternatives;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.e("url", url);

        return url;
    }

    public static boolean validateCityState(String cityStateString) {
        Log.e("checking for valid city, state", cityStateString);

//        boolean checkResult = cityStateString.matches(".*, [A-Z][A-Z]");
        boolean checkResult = cityStateString.matches(".*, ([a-zA-Z]+|[a-zA-Z]+\\\\s[a-zA-Z]+)");

        Log.e("will return", String.valueOf(checkResult));

        return checkResult;
    }
}
