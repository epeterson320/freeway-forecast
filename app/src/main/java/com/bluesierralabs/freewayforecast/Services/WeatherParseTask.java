package com.bluesierralabs.freewayforecast.Services;

import android.os.AsyncTask;

import com.bluesierralabs.freewayforecast.helpers.OpenWeatherParser;
import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.Models.WeatherItem;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by timothy on 1/10/15.
 */
public class WeatherParseTask extends AsyncTask<ArrayList<String>, Integer, WeatherItem>
{
    Trip tripInstance = Trip.getInstance();

    /** Array list for the weather items */
    private ArrayList<WeatherItem> weatherResults = new ArrayList<WeatherItem>();

    // Parsing the data in non-ui thread
    @Override
    protected WeatherItem doInBackground(ArrayList<String>... jsonData) {
        JSONObject jObject;

        // Before adding the the weather items, clear the holder out
        weatherResults.clear();

        // Create a parser object to parse the json returned from the API call
        OpenWeatherParser parser = new OpenWeatherParser();

        try {
            // parse all the json data returned for the hour locations
            for (int i = 0; i < jsonData[0].size(); i++) {
                jObject = new JSONObject(jsonData[0].get(i));

//                    Log.e("ParserTask", "Adding a weather item");
                weatherResults.add(parser.parse(jObject));
//                    publishProgress(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(WeatherItem result) {

        // Add all the results to the listing
        for (int i=0; i<weatherResults.size(); i++) {
            // Also add the weather item to the trip instance
            WeatherItem currentItem = weatherResults.get(i);
            tripInstance.getWeatherItems().get(i).addWeatherInfo(currentItem.getIcon(),
                    currentItem.getMinTemp(), currentItem.getMaxTemp(),
                    currentItem.getTempAsDouble(), currentItem.getTitle(), currentItem.getDetail());
        }

        // Update the adapter with the updated hour listing
//        adapter.setData(tripInstance.getWeatherItems());

        // Setup the weather listing with its adapter
//        weatherListing.setAdapter(adapter);
    }
}
