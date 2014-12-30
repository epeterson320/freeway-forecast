package com.bluesierralabs.freewayforecast.Models;

/**
 * Created by timothy on 12/29/14.
 */
public class Settings {
    private static Settings settingsObject = null;

    /** Default the temperature units to fahrenheit */
    private tempScale tempUnits = tempScale.FAHRENHEIT;

    private boolean useMetricForDistance = false;

    /** Measured in minutes */
    private int weatherInterval = 60;

    public enum tempScale {
        FAHRENHEIT,
        CELSIUS,
        KELVIN
    }

    // Return the instance of the singleton class or create an instance if one does not exist.
    public static Settings getInstance() {
        if (settingsObject == null) {
            settingsObject = new Settings();
        }
        return settingsObject;
    }

    public void setTempUnits(tempScale units) {
        this.tempUnits = units;
    }

    public tempScale getTempUnits() {
        return tempUnits;
    }

    public void setUseMetricForDistance(boolean metric) {
        this.useMetricForDistance = metric;
    }

    public boolean getUseMetricForDistance() {
        return useMetricForDistance;
    }

    public void setWeatherInterval(int interval) {
        this.weatherInterval = interval;
    }

    public int getWeatherInterval() {
        return weatherInterval;
    }
}
