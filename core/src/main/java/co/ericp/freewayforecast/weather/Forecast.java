package co.ericp.freewayforecast.weather;

import java.util.List;

import co.ericp.freewayforecast.LatLng;

/**
 * The weather forecast over a given stretch of time in one place.
 */
public class Forecast {
    LatLng coords;
    List<WeatherPoint> points;
    long startTime;
    long endTime;
}
