package co.ericp.freewayforecast;

import rx.Single;

/**
 * A source for getting the weather.
 *
 * An example implementation of this would be the weather.com API.
 */
public interface WeatherSource {
    Single<Forecast> getForecast(LatLng coords, long time);
}
