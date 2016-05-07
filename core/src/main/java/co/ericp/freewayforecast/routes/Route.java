package co.ericp.freewayforecast.routes;

/**
 * A route at a particular time from a place to a place.
 */
public class Route {
    Object[] legs; // A leg has a Polyline, distance, and duration
    double distance; // distance in meters
    long duration;
    long startTime;
    long endTime;
}
