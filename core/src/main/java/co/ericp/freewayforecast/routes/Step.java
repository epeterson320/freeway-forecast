package co.ericp.freewayforecast.routes;

import java.util.List;

import co.ericp.freewayforecast.LatLng;

/**
 * One step of a trip (one stretch of driving without turns)
 */
public class Step {
    double distance;
    long duration;

    String instructions;

    LatLng start;
    LatLng end;

    List<LatLng> polyline;
}
