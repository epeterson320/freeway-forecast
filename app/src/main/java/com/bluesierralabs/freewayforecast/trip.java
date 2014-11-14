package com.bluesierralabs.freewayforecast;

/**
 * Created by timothy on 11/13/14.
 */
public class trip {
    /** Starting location for the trip */
    private String tripStartAddress;

    /** Destination of the road trip */
    private String tripEndAddress;

    /** Date that the user is going to start driving on their trip */
    private String tripStartDate;

    /** Time of day that the user is going to start driving */
    private String tripStartTime;

    // Set the address for the start of the trip
    public void setTripStartAddress(String address) {
        // TODO: Add some type of checking here so that we don't get bad addresses.
        // TODO: Consider the use of another data type other than a generic string.
        this.tripStartAddress = address;
    }

    // Get the address for the start of the trip
    public String getTripStartAddress() {
        return tripStartAddress;
    }

    // Set the address for the end of the trip
    public void setTripEndAddress(String tripEndAddress) {
        // TODO: Add some type of checking here so that we don't get bad addresses.
        // TODO: Consider the use of another data type other than a generic string.
        this.tripEndAddress = tripEndAddress;
    }

    // Get the address for the end of the trip
    public String getTripEndAddress() {
        return tripEndAddress;
    }

    // Set the start date of the trip
    public void setTripStartDate(String tripStartDate) {
        // TODO: Add checking for past dates, too much in the future, etc.
        // TODO: Consider the use of another data type other than a string
        this.tripStartDate = tripStartDate;
    }

    // Get the start date of the trip
    public String getTripStartDate() {
        return tripStartDate;
    }

    // Set the start time of the trip
    public void setTripStartTime(String tripStartTime) {
        // TODO: Add checking here for bad times and other problematic things
        // TODO: Consider using another data type instead of this string.
        this.tripStartTime = tripStartTime;
    }

    // Get the start time of the trip
    public String getTripStartTime() {
        return tripStartTime;
    }
}
