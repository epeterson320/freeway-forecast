package co.ericp.freewayforecast.routes;

import java.util.List;

import co.ericp.freewayforecast.Location;

/**
 * One leg of a trip. Trips without waypoints only have one leg.
 */
public class Leg {
    double distance;
    long duration;

    Location start;
    Location end;

    List<Step> steps;
}
