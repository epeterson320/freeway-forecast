package com.bluesierralabs.freewayforecast.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.bluesierralabs.freewayforecast.Helpers.App;
import com.bluesierralabs.freewayforecast.R;
import com.bluesierralabs.freewayforecast.SettingsActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by timothy on 11/24/14.
 */
public class WeatherItem {
//    private Context mContext;

    /** Application resources */
    private Resources resources = App.getContext().getResources();

    private SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());

    private Drawable icon;
    public String title;
    public String detail;
    public Double temp;

    private final String location;
    private final float minTemp;
    private final float maxTemp;

    // Class constructor with arguments
    public WeatherItem(Drawable icon, String title, String detail, double temp) {
        super();
        this.icon = icon;
        this.title = title;
        this.detail = detail;
        this.temp = temp; // + "°";

        this.location = "somewhere";
        this.minTemp = 0;
        this.maxTemp = 0;
    }

    public WeatherItem(Drawable icon, String location, float minTemp, float maxTemp) {
        super();
        this.icon = icon;
        this.location = location;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getLocation() {
        return this.location;
    }

    /**
     * Get the weather item's temperature as a string with the degree symbol
     * @return String
     */
    public String getTemp() {
        // Get the temperature scale from the settings
        String temperatureScale = sharedPref.getString("pref_temperature", "NULL");

        // Create a new double for the converted temperature
        Double temperatureConverted = temp;

        // Determine the conversion type
        if(temperatureScale.equals(resources.getString(R.string.temp_fahrenheit))) {
            // Convert the temperature to Fahrenheit
            temperatureConverted = (temp - 273.15) * 1.8000 + 32.00;
        } else if (temperatureScale.equals(resources.getString(R.string.temp_celsius))) {
            // Convert the temperature to Celsius
            temperatureConverted = temp - 273.15;
        }
        // Otherwise, keep the temperature in kelvin.

        // Round the temperature to one decimal place
        temperatureConverted = round(temperatureConverted, 1);

        // Return the temperature as a string with the little 'degree' symbol
        return temperatureConverted + "°";
    }

    public float getMinTemp() {
        return this.minTemp;
    }

    public float getMaxTemp() {
        return this.maxTemp;
    }

    /**
     * Round a double to a specified number of decimal places. Based on answer on stackoverflow
     * http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
     * @param value
     * @param places
     * @return
     */
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
