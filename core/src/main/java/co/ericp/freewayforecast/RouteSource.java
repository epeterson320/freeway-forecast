package co.ericp.freewayforecast;

import java.util.List;

import rx.Single;

/**
 * A source for getting directions from place to place.
 *
 * An example implementation of this interface would be Google Maps.
 */
public interface RouteSource {
    /**
     * Get the possible routes from one place to another.
     *
     * The parameters are {@link Location} objects. These may have a name,
     * coordinates, or both. If the name is empty or null, the coordinates
     * will be used. If the coordinates are not a valid latitude and
     * longitude, the name will be used. If neither the name nor the
     * coordinates are valid, an empty list will be returned.
     *
     * @param origin the location from which to start
     * @param destination the location at which to arrive
     * @return a list of possible routes, asynchronously
     */
    Single<List<Route>> getRoutes(Location origin, Location destination);
}
