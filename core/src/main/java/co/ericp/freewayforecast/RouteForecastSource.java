package co.ericp.freewayforecast;

import java.util.List;

import rx.Single;

/**
 * The main API to our business logic.
 *
 * This takes a start and end location and departure time as parameters
 * and returns a list of routes, along with their weather conditions along the
 * way.
 */
public interface RouteForecastSource {
    Single<List<RouteForecast>> getRouteForecast(
            Location origin, Location destination, long departure);
}
