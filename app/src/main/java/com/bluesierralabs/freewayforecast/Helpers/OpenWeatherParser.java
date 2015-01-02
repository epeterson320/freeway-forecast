package com.bluesierralabs.freewayforecast.Helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by timothy on 1/1/15.
 * Parser class for getting weather information returned from the Open Weather Map API
 */
public class OpenWeatherParser {
    public void parse(JSONObject jWeatherObject) {

        // Weather items for the hour marker location
        String description = "";    // main - one word description of the weather
        String descriptionExplain = ""; // description - a longer explanation of the weather
        Double temp;
        Double temp_min;
        Double temp_max;

        try {
            String locationName = jWeatherObject.getString("name");
            Log.e("Got location name", locationName);

            // Get information from the 'weather' section of Open Weather Map's return
            JSONArray weatherItems = jWeatherObject.getJSONArray("weather");
            for (int i=0; i<weatherItems.length(); i++){
                JSONObject weatherObject = (JSONObject) weatherItems.get(i);

                description = weatherObject.getString("main");
                descriptionExplain = weatherObject.getString("description");

                Log.e("description", descriptionExplain);
                Log.e("main", description);
            }

            // Get information from the 'main' section of Open Weather Map's return
            JSONObject mainItems = jWeatherObject.getJSONObject("main");

            // Get temperature values
            temp = mainItems.getDouble("temp");
            temp_min =  mainItems.getDouble("temp_min");
            temp_max = mainItems.getDouble("temp_max");

            Log.e("temp", "" + temp + " " + temp_min + " " + temp_max);

            // TODO: Should i get wind speeds?

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
