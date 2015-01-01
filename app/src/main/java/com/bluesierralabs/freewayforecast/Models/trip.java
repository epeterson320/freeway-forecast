package com.bluesierralabs.freewayforecast.Models;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.bluesierralabs.freewayforecast.Helpers.App;
import com.bluesierralabs.freewayforecast.R;
import com.bluesierralabs.freewayforecast.Tasks.GetCoordinatesTask;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private Date tripStart;

    private Resources resources = App.getContext().getResources();

    /** List of latitude and longitude points where the trip hours markers will occur */
    private List<LatLng> hourMarkers;

//    private Resources resources = App.getContext().getResources();

    // Class constructor
    private Trip() {
        // TODO: Determine if there should be any construction in the class initialization.

        Log.e("Creating trip instance", "Okay");

//        this.hourMarkers.clear();

        // Set the trip start date to the current time
        Calendar cal = Calendar.getInstance();
        Date currentTime = new Date();  // Initializes this Date instance to the current time.
        cal.setTime(currentTime);
        this.tripStart = cal.getTime();
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

    public Date getTripStart() {
        return tripStart;
    }

    /**
     * Set the trip's start time with year, month, and day
     *
     * @param year
     * @param month
     * @param day
     */
    public void setTripStartWithDateSelect(int year, int month, int day) {
        // First convert the date object to a calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTime(tripStart);

        // Now change the year, month, and date
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, day);

        // Finally convert the calendar back to a date, and save it for the trip
        this.tripStart = cal.getTime();
    }

    /**
     * Set the trip's start time with hours and minutes
     *
     * @param hour
     * @param minute
     */
    public void setTripStartWithTimeSelect(int hour, int minute) {
        // First convert the date object to a calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTime(tripStart);

        // Now change the hour and minutes
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.MILLISECOND, 0);

        // Finally convert the calendar back to a date, and save it for the trip
        this.tripStart = cal.getTime();
    }

    public String getTripStartDayName() {
        // First convert the date object to a calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTime(tripStart);

        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return resources.getString(R.string.sunday);
            case Calendar.MONDAY:
                return resources.getString(R.string.monday);
            case Calendar.TUESDAY:
                return resources.getString(R.string.tuesday);
            case Calendar.WEDNESDAY:
                return resources.getString(R.string.wednesday);
            case Calendar.THURSDAY:
                return resources.getString(R.string.thursday);
            case Calendar.FRIDAY:
                return resources.getString(R.string.friday);
            case Calendar.SATURDAY:
                return resources.getString(R.string.saturday);
            default:
                return null;
        }
    }

    public String getTripStartDayNumber() {
        // First convert the date object to a calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTime(tripStart);
        int number = cal.get(Calendar.DAY_OF_MONTH);
        String numberString = "" + number;

        if (number == 1) {
            numberString = numberString + "st";
        } else if (number == 2) {
            numberString = numberString + "nd";
        } else if (number == 3) {
            numberString = numberString + "rd";
        } else  {
            numberString = numberString + "th";
        }

        return numberString;
    }

    public String getTripStartMonthName() {
        // First convert the date object to a calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTime(tripStart);
        int monthNumber = cal.get(Calendar.MONTH);
        switch (monthNumber) {
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
            default:
                return null;
        }
    }

    public String getTripStartTimeReadable() {
        // First convert the date object to a calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTime(tripStart);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String readableTime = "";

        String minuteString = "" + minute;
        if (minute < 10) {
            minuteString = "0" + minuteString;
        }

        readableTime = "" + hour + ":" + minuteString;
        if (hour < 12) {
            readableTime = readableTime + " AM";
        } else {
            readableTime = readableTime + " PM";
        }

        return readableTime;
    }
}
