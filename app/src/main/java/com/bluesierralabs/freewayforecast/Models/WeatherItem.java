package com.bluesierralabs.freewayforecast.Models;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.bluesierralabs.freewayforecast.Helpers.App;
import com.bluesierralabs.freewayforecast.Helpers.Utilities;
import com.bluesierralabs.freewayforecast.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by timothy on 11/24/14.
 */
public class WeatherItem {

    /** Application resources for getting string values */
    private Resources resources = App.getContext().getResources();

    /** Application preferences for formatting and displaying data */
    private SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());

    /** Icon for the weather item */
    private Drawable icon;

    /** Title for the weather item */
    private String title;

    /** More detail for the weather item */
    private String detail;

    /** Temperature for the weather item */
    private Double temp;

    /** Minimum temperature for the weather item - not sure if I will use this */
    private Double minTemp;

    /** Maximum temperature for the weather item - not sure if I will use this */
    private Double maxTemp;

    /** Date that the weather item corresponds to */
    private Date time;

    /** Latitude and Latitude location of the weather time */
    private LatLng location;

    /**
     * Default constructor
     */
    public WeatherItem() {
        // Do nothing
    }

    public WeatherItem(LatLng itemLocation) {
        this.location = itemLocation;
    }


    public void setIcon(Drawable weatherIcon) {
        this.icon = weatherIcon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setTitle(String weatherTitle) {
        this.title = weatherTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setDetail(String weatherDetail) {
        this.detail = weatherDetail;
    }

    public String getDetail() {
        return detail;
    }

    public void setTemp(Double weatherTemp) {
        this.temp = weatherTemp;
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
        temperatureConverted = Utilities.roundDouble(temperatureConverted, 1);

        // Return the temperature as a string with the little 'degree' symbol
        return temperatureConverted + "°";
    }

    public Double getTempAsDouble() {
        return temp;
    }

    public String getTime() {
        // Get the hour-minute time in a string object
        return Utilities.getTimeStringFromDate(time);
    }

    public void setDate(Date weatherItemTime) {
        this.time = weatherItemTime;
    }

    public void setLocation(LatLng markerLocation) {
        this.location = markerLocation;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setMinTemp(Double weatherMinTemp) {
        this.minTemp = weatherMinTemp;
    }

    public Double getMinTemp() {
        return minTemp;
    }

    public void setMaxTemp(Double weatherMaxTemp) {
        this.maxTemp = weatherMaxTemp;
    }

    public Double getMaxTemp() {
        return maxTemp;
    }
}
