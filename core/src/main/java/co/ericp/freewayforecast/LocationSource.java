package co.ericp.freewayforecast;

import java.util.List;

import rx.Single;

/**
 * A mechanism to get locations.
 */
public interface LocationSource {
    Single<List<Location>> getCurrentLocation();
    Single<List<Location>> getLocation(String query);
}
