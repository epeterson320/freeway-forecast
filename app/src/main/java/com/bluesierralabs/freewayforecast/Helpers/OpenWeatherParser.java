package com.bluesierralabs.freewayforecast.Helpers;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bluesierralabs.freewayforecast.Models.WeatherItem;
import com.bluesierralabs.freewayforecast.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by timothy on 1/1/15.
 * Parser class for getting weather information returned from the Open Weather Map API
 * http://openweathermap.org/weather-data#current
 */
public class OpenWeatherParser {

    private Resources resources = App.getContext().getResources();

    private WeatherItem newMarker = null;

    public WeatherItem parse(JSONObject jWeatherObject) {

        // Weather items for the hour marker location
        int apiCode = 0;
        String description = "";    // main - one word description of the weather
//        String descriptionExplain = ""; // description - a longer explanation of the weather
        Double temp;
        Double temp_min;
        Double temp_max;

        try {
            String locationName = jWeatherObject.getString("name");
            Log.e("Got location name", locationName);

            // Get information from the 'weather' section of Open Weather Map's return
            JSONArray weatherItems = jWeatherObject.getJSONArray("weather");
            for (int i = 0; i < weatherItems.length(); i++) {
                JSONObject weatherObject = (JSONObject) weatherItems.get(i);

                apiCode = weatherObject.getInt("id");
                description = weatherObject.getString("main");
//                descriptionExplain = weatherObject.getString("description");
            }

            // Get information from the 'main' section of Open Weather Map's return
            JSONObject mainItems = jWeatherObject.getJSONObject("main");

            // Get temperature values
            temp = mainItems.getDouble("temp");
            temp_min = mainItems.getDouble("temp_min");
            temp_max = mainItems.getDouble("temp_max");

            // Determine the weather type based on http://openweathermap.org/weather-conditions
            Drawable icon;
            if ((apiCode >= 200) && (apiCode < 300)) {
                // Thunderstorm section
                icon = resources.getDrawable(R.drawable.ic_weather_lightning);
            } else if ((apiCode >= 300) && (apiCode < 600)) {
                // Drizzle section
                icon = resources.getDrawable(R.drawable.ic_weather_pouring);
            } else if ((apiCode >= 600) && (apiCode < 700)) {
                // Snow section
                icon = resources.getDrawable(R.drawable.ic_weather_snow);
            } else if ((apiCode >= 700) && (apiCode < 800)) {
                // Atmosphere section
                icon = resources.getDrawable(R.drawable.ic_action_cancel);
            } else if (apiCode == 800) {
                // Clear
                icon = resources.getDrawable(R.drawable.ic_weather_sunny);
            } else if ((apiCode > 800) && (apiCode < 900)) {
                // Cloudy section
                icon = resources.getDrawable(R.drawable.ic_weather_partlycloudy);
            } else {
                icon = resources.getDrawable(R.drawable.ic_action_cancel);
            }

            // TODO: Should i get wind speeds?

            // Create a new weather item and add what we have.
            newMarker = new WeatherItem();
            newMarker.setIcon(icon);
            newMarker.setTitle(locationName);
            newMarker.setDetail(description);
            newMarker.setTemp(temp);
            newMarker.setMinTemp(temp_min);
            newMarker.setMaxTemp(temp_max);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newMarker;
    }

    public WeatherItem parseForecastIo(JSONObject jWeatherObject) {
        double time;
        String summary;
        String iconText = null;
//        int precipIntensity;
//        int precipProbability;
        double temp = 0;
//        double apparentTemp;
//        double visibility;
//        double cloudCover;
        Drawable icon;

        try {
            // Get the hourly information from the json response
            JSONObject hourly = jWeatherObject.getJSONObject("hourly");

            // Get the hours as an array and start parsing
            JSONArray hours = hourly.getJSONArray("data");

            Log.e("parseForecastIo", "" + hours.length() + " hours of data");

            for (int i=0; i < hours.length(); i++) {
                JSONObject hour = hours.getJSONObject(i);

                time = hour.getDouble("time");
//                summary = hour.getString("summary");
                iconText = hour.getString("icon");
//                precipIntensity = hour.getInt("precipIntensity");
//                precipProbability = hour.getInt("precipProbability");
                temp = hour.getDouble("temperature");
//                apparentTemp = hour.getDouble("apparentTemperature");
//                "dewPoint":50.47,
//                "humidity":0.88,
//                "windSpeed":6.21,
//                "windBearing":326,
//                visibility = hour.getDouble("visibility");
//                cloudCover = hour.getDouble("cloudCover");

                Log.e("forecastIO hour object", "time=" + String.valueOf(time) + " temp="
                        + String.valueOf(temp) + " weather=" + iconText);
            }

        } catch(JSONException e) {
            Log.e("parseForecastIo", "JSONException");
        }

        // Determine what icon to use

        if (iconText == null) {
            iconText = "question";
        }

//        clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        if (iconText == "clear-day") {
            icon = resources.getDrawable(R.drawable.ic_weather_sunny);
        } else if (iconText == "clear-night") {
            // TODO: Need to get some night icons
            icon = resources.getDrawable(R.drawable.ic_weather_sunny);
        } else if (iconText == "rain") {
            icon = resources.getDrawable(R.drawable.ic_weather_pouring);
        } else if (iconText == "snow") {
            icon = resources.getDrawable(R.drawable.ic_weather_snow);
        } else if (iconText == "sleet") {
            // TODO: Need to look into a sleet icon
            icon = resources.getDrawable(R.drawable.ic_weather_snow);
        } else if (iconText == "wind") {
            // TODO: Need to get a wind icon
            icon = resources.getDrawable(R.drawable.ic_action_cancel);
        } else if (iconText == "fog") {
            // TODO: Need to get a foggy icon
            icon = resources.getDrawable(R.drawable.ic_weather_cloudy);
        } else if (iconText == "cloudy") {
            icon = resources.getDrawable(R.drawable.ic_weather_cloudy);
        } else if (iconText == "partly-cloudy-day") {
            icon = resources.getDrawable(R.drawable.ic_weather_partlycloudy);
        } else if (iconText == "partly-cloudy-night") {
            // TODO: Need to get a night partly cloudy icon
            icon = resources.getDrawable(R.drawable.ic_weather_partlycloudy);
        } else {
            Log.e("parseForecastIo", "no icon for " + iconText);
            icon = resources.getDrawable(R.drawable.ic_action_cancel);
        }

        // Create a new weather item and add what we have.
        newMarker = new WeatherItem();
        newMarker.setIcon(icon);
//        newMarker.setTitle(locationName);
//        newMarker.setDetail(description);
        newMarker.setTemp(temp);
//        newMarker.setMinTemp(temp_min);
//        newMarker.setMaxTemp(temp_max);

        return newMarker;
    }
}
