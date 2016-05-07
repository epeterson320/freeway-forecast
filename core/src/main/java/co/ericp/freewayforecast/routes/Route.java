package co.ericp.freewayforecast.routes;

import java.util.List;

import co.ericp.freewayforecast.LatLng;
import co.ericp.freewayforecast.Location;

/**
 * A route at a particular time from a place to a place.
 */
public class Route {
    double distance; // distance in meters
    long duration;
    long startTime;
    long endTime;
    LatLng neBound;
    LatLng swBound;
    Location origin;
    Location destination;

    List<Leg> legs;
}
