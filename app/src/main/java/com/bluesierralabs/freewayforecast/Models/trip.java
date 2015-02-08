package com.bluesierralabs.freewayforecast.Models;

import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.bluesierralabs.freewayforecast.Helpers.App;
import com.bluesierralabs.freewayforecast.Helpers.Utilities;
import com.bluesierralabs.freewayforecast.R;
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

    /** Application resources */
    private Resources resources = App.getContext().getResources();

    /** Starting location for the trip */
    private String tripStartAddress;

    /** Starting coordinates location for the trip */
    private LatLng tripStartCoordinates;

    /** Destination of the road trip */
    private String tripEndAddress;

    /** Destination coordinates of the road trip */
    private LatLng tripEndCoordinates;

    /** Date Object for the trip start time */
    private Date tripStart;

    /** Integer for the duration between weather items */
    private long tripIntervals = 3600000;

    /** Weather items associated with the hour markers **/
    private ArrayList<WeatherItem> weatherItems;

    // Class constructor
    private Trip() {

        // Initialize the array lists
        this.weatherItems = new ArrayList<WeatherItem>();

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

    /**
     * Clear all the hour markers and weather items from the trip instance. This function is
     * intended to be used when the user returns to the splash screen - either they wish to edit the
     * trip or start over. This function helps ensures everything gets set back to start.
     */
    public void clear() {
        // Clear the weather array lists
        this.weatherItems.clear();

        // Set the trip start date to the current time
        Calendar cal = Calendar.getInstance();
        Date currentTime = new Date();  // Initializes this Date instance to the current time.
        cal.setTime(currentTime);
        this.tripStart = cal.getTime();
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
                return resources.getString(R.string.january);
            case Calendar.FEBRUARY:
                return resources.getString(R.string.february);
            case Calendar.MARCH:
                return resources.getString(R.string.march);
            case Calendar.APRIL:
                return resources.getString(R.string.april);
            case Calendar.MAY:
                return resources.getString(R.string.may);
            case Calendar.JUNE:
                return resources.getString(R.string.june);
            case Calendar.JULY:
                return resources.getString(R.string.july);
            case Calendar.AUGUST:
                return resources.getString(R.string.august);
            case Calendar.SEPTEMBER:
                return resources.getString(R.string.september);
            case Calendar.OCTOBER:
                return resources.getString(R.string.october);
            case Calendar.NOVEMBER:
                return resources.getString(R.string.november);
            case Calendar.DECEMBER:
                return resources.getString(R.string.december);
            default:
                return null;
        }
    }

    public String getTripStartDateReadable() {
        String inputDateString = "" + getTripStartDayName() + ", "
                + getTripStartMonthName() + " " + getTripStartDayNumber();

        return inputDateString;
    }

    public String getTripStartTimeReadable() {
        // Get the hour-minute of the date returned as a string object
        return Utilities.getTimeStringFromDate(tripStart);
    }

    public void addTripWeatherItem(WeatherItem weather) {
        // Determine how many weather items are currently in the trip
        int items = this.weatherItems.size();

        // Set a Date object for the weather item based on the trip start time and item position
        Date itemTime = new Date(this.tripStart.getTime() + (this.tripIntervals * items));

        // Add the time corresponding to the weather item.
        weather.setDate(itemTime);

        // Now add the weather item to the trip
        this.weatherItems.add(weather);
    }

    public void removeTripWeatherItem(int item) {
        Log.e("TripInstance", "Removing time associated with route " + getWeatherItems().get(item).getRouteNumber());
        this.weatherItems.remove(item);
    }

    public ArrayList<WeatherItem> getWeatherItems() {
        return weatherItems;
    }

    public WeatherItem getWeatherItem (int itemNum) {
        return weatherItems.get(itemNum);
    }
}
