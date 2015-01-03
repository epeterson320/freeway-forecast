package com.bluesierralabs.freewayforecast.Models;

import android.graphics.drawable.Drawable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by timothy on 11/24/14.
 */
public class WeatherItem {
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

    public String getTemp() {

        int type = 0; // 0 = F, 1 = C, 2 = K

        Double temperatureConverted = temp;

        if(type == 0) {
            temperatureConverted = (temp - 273.15) * 1.8000 + 32.00;
        } else if (type == 1) {
            temperatureConverted = temp - 273.15;
        }
        // Otherwise, keep the temperature in kelvin.

        // Round the temperature to one decimal place
        temperatureConverted = round(temperatureConverted, 1);

        return temperatureConverted + "°";
    }

    public float getMinTemp() {
        return this.minTemp;
    }

    public float getMaxTemp() {
        return this.maxTemp;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
