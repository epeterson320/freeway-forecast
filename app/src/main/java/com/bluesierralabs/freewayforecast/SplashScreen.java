package com.bluesierralabs.freewayforecast;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.bluesierralabs.freewayforecast.Helpers.DateSelectFragment;
import com.bluesierralabs.freewayforecast.Helpers.TimeSelectFragment;
import com.bluesierralabs.freewayforecast.Helpers.Utilities;
import com.bluesierralabs.freewayforecast.Models.Trip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.AdapterView.OnFocusChangeListener;

public class SplashScreen extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private Trip mTrip;
    private AutoCompleteTextView mFrom;
    private AutoCompleteTextView mDest;
    private EditText mDate;
    private EditText mTime;

    /** Boolean to track if the start location field was correctly supplied */
    private boolean startLocationGood = false;

    /** Boolean to track if the end location field was supplied */
    private boolean endLocationGood = false;

    // locations objects
    LocationClient mLocationClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mFrom = (AutoCompleteTextView) findViewById(R.id.tripStartAddress);
        mDest = (AutoCompleteTextView) findViewById(R.id.tripEndAddress);
        mDate = (EditText) findViewById(R.id.tripStartDate);
        mTime = (EditText) findViewById(R.id.tripStartTime);
        mTrip = Trip.getInstance();

        // Set the default application preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // Auto complete example from
        // http://www.tutorialspoint.com/android/android_auto_complete.htm

        // Setup the listeners and handlers for the trip start location edit text box
        mFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Hide the virtual keyboard if an item is selected
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mFrom.getWindowToken(), 0);
            }
        });

        mFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Start location", s.toString());
                startLocationGood = Utilities.validateCityState(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s){ }
        });

        // Setup the listeners and handlers for the trip end location edit text box
        mDest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Hide the virtual keyboard if an item is selected
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mDest.getWindowToken(), 0);
            }
        });

        // Listen for when the trip end's location gets edited.
        mDest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("End location changed to", s.toString());
                endLocationGood = Utilities.validateCityState(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Setup the auto complete of cities based on the user's location.
        String[] countries = getResources().getStringArray(R.array.cities_usa);
        ArrayAdapter adapter = new ArrayAdapter (this,android.R.layout.simple_list_item_1,countries);

        // Set the adapter for the start and end trip fields
        mFrom.setAdapter(adapter);
        mDest.setAdapter(adapter);

        // On create, clear any focus that was present
        mDate.clearFocus();

        // If the date entry field gains focus, show the DateSelectFragment
        mDate.setKeyListener(null);
        mDate.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment newFragment = new DateSelectFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");


                }
            }
        });

        // If the time entry field gains focus, show the TimeSelectFragment
        mTime.setKeyListener(null);
        mTime.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment newFragment = new TimeSelectFragment();
                    newFragment.show(getSupportFragmentManager(), "timePicker");

                }
            }
        });

        // 2. create LocationClient
        mLocationClient = new LocationClient(this, this, this);

        // 3. create & set LocationRequest for Location update
        mLocationRequest = LocationRequest.create();

        // Only poll the current location once
        mLocationRequest.setNumUpdates(1);
        // considered to be about 100 meter accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(1000 * 10);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(1000 * 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 1. connect the client.
        mLocationClient.connect();

        Log.e("SplashScreen", "onStart");

        // When the SplashScreen Activity is started, clear out anything in the trip object. Every
        // setting is going to change
        mTrip.clear();

        // Set up the date (Day, Month) for the splash screen
        mDate.setText(mTrip.getTripStartDateReadable());

        // Set up the time (Hours and Minutes) for the splash screen
        mTime.setText(mTrip.getTripStartTimeReadable());

        // Make sure that we flag the start and end locations as "not provided"
        startLocationGood = false;
        endLocationGood = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 1. disconnecting the client invalidates it.
        mLocationClient.disconnect();

        Log.e("SplashScreen", "onStop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void submitTimesAndPlaces(View view) {

        if (startLocationGood && endLocationGood) {

            // Get the starting address for the trip from the input box
            String startAddress = mFrom.getText().toString();
            Log.e("start address", startAddress);
            mTrip.setTripStartAddress(startAddress, this);

            // Get the ending address for the trip from the input box
            String endAddress = mDest.getText().toString();
            Log.e("end address", endAddress);
            mTrip.setTripEndAddress(endAddress, this);

            // Get the date of the trip from the input box
            String date = mDate.getText().toString();
            //mTrip.setTripStartDate(date);

            // Get the time of the trip from the input box
            String time = mTime.getText().toString();
            //mTrip.setTripStartTime(time);

            // Go to the route select activity
            Intent routeIntent = new Intent(this, RouteSelectActivity.class);
            startActivity(routeIntent);

        } else if (!startLocationGood) {
            CharSequence text = "Please supply a trip start location";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        } else if (!endLocationGood) {
            CharSequence text = "Please supply a trip end location";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    // From: http://hmkcode.com/android-get-current-location-location-updates-location-services-api-tutorial/
    @Override
    public void onConnected(Bundle bundle) {
        if(mLocationClient != null)
            mLocationClient.requestLocationUpdates(mLocationRequest,  this);

        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        if(mLocationClient != null){
            // get location
            mCurrentLocation = mLocationClient.getLastLocation();
            try {
                String city = "";
                String state = "";

                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                if (addresses.size() > 0) {
                    city = String.valueOf(addresses.get(0).getLocality());
                    state = String.valueOf(addresses.get(0).getAdminArea());
                    mFrom.setText((city + ", " + state));
                }
            } catch(NullPointerException npe) {

                Toast.makeText(this, "Failed to Connect", Toast.LENGTH_SHORT).show();

                // switch on location service intent
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    // From: http://hmkcode.com/android-get-current-location-location-updates-location-services-api-tutorial/
    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected.", Toast.LENGTH_SHORT).show();
    }

    // From: http://hmkcode.com/android-get-current-location-location-updates-location-services-api-tutorial/
    @Override
    public void onLocationChanged(Location location) {
        // For now there is no need to do anything different on location change
    }

    // From: http://hmkcode.com/android-get-current-location-location-updates-location-services-api-tutorial/
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }
}
