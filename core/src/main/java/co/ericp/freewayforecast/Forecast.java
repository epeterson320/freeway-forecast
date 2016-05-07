package co.ericp.freewayforecast;

import java.util.List;

/**
 * The weather forecast over a given stretch of time in one place.
 */
public class Forecast {
    LatLng coords;
    List<WeatherPoint> points;
    long startTime;
    long endTime;
}
