package co.ericp.freewayforecast.weather;

import co.ericp.freewayforecast.LatLng;

/**
 * A the weather at a specific time and place.
 */
public class WeatherPoint {
    LatLng coords;
    long time;
    double temp;
    int status;
}
