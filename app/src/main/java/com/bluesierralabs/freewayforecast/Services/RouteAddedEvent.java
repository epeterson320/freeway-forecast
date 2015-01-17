package com.bluesierralabs.freewayforecast.Services;

/**
 * Created by timothy on 1/17/15.
 */
public class RouteAddedEvent {
    public final String description;

    public RouteAddedEvent(String description) {
        this.description = description;
    }

    @Override public String toString() {
        return description;
    }
}