package co.ericp.freewayforecast.weather;

import co.ericp.freewayforecast.LatLng;
import co.ericp.freewayforecast.weather.Forecast;
import rx.Single;

/**
 * A source for getting the weather.
 *
 * An example implementation of this would be the weather.com API.
 */
public interface WeatherSource {
    Single<Forecast> getForecast(LatLng coords, long time);
}
