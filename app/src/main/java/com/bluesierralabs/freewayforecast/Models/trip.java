package com.bluesierralabs.freewayforecast.Models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.bluesierralabs.freewayforecast.Tasks.GetCoordinatesTask;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by timothy on 11/13/14.
 */
public class Trip {
    /** Object of the trip class */
    private static Trip tripObject = null;

    /** Starting location for the trip */
    private String tripStartAddress;

    /** Starting coordinates location for the trip */
    private LatLng tripStartCoordinates;

    /** Destination of the road trip */
    private String tripEndAddress;

    /** Destination coordinates of the road trip */
    private LatLng tripEndCoordinates;

    /** Date that the user is going to start driving on their trip */
    private String tripStartDate;

    /** Time of day that the user is going to start driving */
    private String tripStartTime;

    /** List of latitude and longitude points where the trip hours markers will occur */
    private List<LatLng> hourMarkers;

    // Class constructor
    private Trip() {
        // TODO: Determine if there should be any construction in the class initialization.

        Log.e("Creating trip instance", "Okay");

//        this.hourMarkers.clear();
    }

    // Return the instance of the singleton class or create an instance if one does not exist.
    public static Trip getInstance() {
        if (tripObject == null) {
            tripObject = new Trip();
        }
        return tripObject;
    }

    // Set the address for the start of the trip
    public void setTripStartAddress(String address, Context context) {
        // TODO: Add some type of checking here so that we don't get bad addresses.
        // TODO: Consider the use of another data type other than a generic string.
        this.tripStartAddress = address;

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses= null; // get the found Address Objects
        try {
            addresses = gc.getFromLocationName(address, 5);

            List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
            for(Address a : addresses){
                if(a.hasLatitude() && a.hasLongitude()){
                    ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                }
            }
            Log.e("Got start coordinate","" + ll.get(0));
            this.tripStartCoordinates = ll.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            this.tripStartCoordinates = null;
        }
    }

    // Get the address for the start of the trip
    public String getTripStartAddress() {
        return tripStartAddress;
    }

    public LatLng getTripStartCoordinates() {
        return this.tripStartCoordinates;
    }

    // Set the address for the end of the trip
    public void setTripEndAddress(String tripEndAddress, Context context) {
        // TODO: Add some type of checking here so that we don't get bad addresses.
        // TODO: Consider the use of another data type other than a generic string.
        this.tripEndAddress = tripEndAddress;

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses= null; // get the found Address Objects
        try {
            addresses = gc.getFromLocationName(tripEndAddress, 5);

            List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
            for(Address a : addresses){
                if(a.hasLatitude() && a.hasLongitude()){
                    ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                }
            }
            Log.e("Got end coordinate","" + ll.get(0));
            this.tripEndCoordinates = ll.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            this.tripEndCoordinates = null;
        }
    }

    // Get the address for the end of the trip
    public String getTripEndAddress() {
        return tripEndAddress;
    }

    public LatLng getTripEndCoordinates() {
        return this.tripEndCoordinates;
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

    public void addHourMarker(LatLng marker) {
        hourMarkers.add(marker);
    }

    public List<LatLng> getHourMarkers() {
        return hourMarkers;
    }
}
