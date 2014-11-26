package com.bluesierralabs.freewayforecast.Models;

/**
 * Created by timothy on 11/24/14.
 */
public class WeatherItem {
    private int icon;
    public String title;
    public String detail;
    public String temp;

    private final String location;
    private final float minTemp;
    private final float maxTemp;

    // Class constructor with arguments
    public WeatherItem(int icon, String title, String detail, int temp) {
        super();
        this.icon = icon;
        this.title = title;
        this.detail = detail;
        this.temp = temp + "°";

        this.location = "somewhere";
        this.minTemp = 0;
        this.maxTemp = 0;
    }

    public WeatherItem(int icon, String location, float minTemp, float maxTemp) {
        super();
        this.icon = icon;
        this.location = location;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getLocation() {
        return this.location;
    }

    public float getMinTemp() {
        return this.minTemp;
    }

    public float getMaxTemp() {
        return this.maxTemp;
    }
}
