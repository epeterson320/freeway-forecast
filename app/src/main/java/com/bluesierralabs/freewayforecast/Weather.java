package com.bluesierralabs.freewayforecast;

/**
 * Created by timothy on 11/24/14.
 */
public class Weather {
    public int icon;
    public String title;
    public String detail;
    public String temp;
    public Weather() {
        super();
    }

    public Weather(int icon, String title, String detail, int temp) {
        super();
        this.icon = icon;
        this.title = title;
        this.detail = detail;
        this.temp = temp + "°";
    }
}
