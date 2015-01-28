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
import java.util.List;
import java.util.Locale;

import static android.widget.AdapterView.OnFocusChangeListener;

public class SplashScreen extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    // Object of the trip class.
    private Trip tripInstance = Trip.getInstance();

    private AutoCompleteTextView startAutoComplete;

    private AutoCompleteTextView endAutoComplete;

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

        // Set the default application preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // Auto complete example from
        // http://www.tutorialspoint.com/android/android_auto_complete.htm

        // Setup the listeners and handlers for the trip start location edit text box
        startAutoComplete = (AutoCompleteTextView) findViewById(R.id.tripStartAddress);
        startAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Hide the virtual keyboard if an item is selected
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(startAutoComplete.getWindowToken(), 0);
            }
        });

        startAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO: Auto-generated method stub
                Log.e("Start location, beforeTextChanged", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Start location changed to", s.toString());
//                if (s.length() > 0) {
//                    startLocationGood = true;
//
////                    .*, [A-Z][A-Z]
//                }
                startLocationGood = Utilities.validateCityState(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: Auto-generated method stub
                Log.e("Start location, afterTextChanged", s.toString());
            }
        });

        // Setup the listeners and handlers for the trip end location edit text box
        endAutoComplete = (AutoCompleteTextView) findViewById(R.id.tripEndAddress);
        endAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Hide the virtual keyboard if an item is selected
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(endAutoComplete.getWindowToken(), 0);
            }
        });

        // Listen for when the trip end's location gets edited.
        endAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("End location changed to", s.toString());
//                if (s.length() > 0) {
//                    endLocationGood = true;
//                }
                endLocationGood = Utilities.validateCityState(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        // Setup the auto complete of cities based on the user's location.
        String[] countries = getResources().getStringArray(R.array.cities_usa);
        ArrayAdapter adapter = new ArrayAdapter (this,android.R.layout.simple_list_item_1,countries);

        // Set the adapter for the start and end trip fields
        startAutoComplete.setAdapter(adapter);
        endAutoComplete.setAdapter(adapter);

        // On create, clear any focus that was present
        findViewById(R.id.tripStartDate).clearFocus();

        // If the date entry field gains focus, show the DateSelectFragment
        ((EditText) findViewById(R.id.tripStartDate)).setKeyListener(null);
        (findViewById(R.id.tripStartDate)).setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    DialogFragment newFragment = new DateSelectFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");


                }
            }
        });

        // If the time entry field gains focus, show the TimeSelectFragment
        ((EditText) findViewById(R.id.tripStartTime)).setKeyListener(null);
        (findViewById(R.id.tripStartTime)).setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
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
        tripInstance.clear();

        // Set up the date (Day, Month) for the splash screen
        EditText inputDate = (EditText) findViewById(R.id.tripStartDate);
        inputDate.setText(tripInstance.getTripStartDateReadable());

        // Set up the time (Hours and Minutes) for the splash screen
        EditText inputTime = (EditText) findViewById(R.id.tripStartTime);
        inputTime.setText(tripInstance.getTripStartTimeReadable());

        // Make sure that we flag the start and end locations as "not provided"
        startLocationGood = false;
        endLocationGood = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.e("SplashScreen", "onRestart");
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
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DateSelectFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimeSelectFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    public void submitTimesAndPlaces(View view) {

        if (startLocationGood && endLocationGood) {

            // Get the starting address for the trip from the input box
            EditText inputStartAddress = (EditText) findViewById(R.id.tripStartAddress);
            String startAddress = inputStartAddress.getText().toString();

            Log.e("start address", startAddress);

            // Pass the context as well.
            tripInstance.setTripStartAddress(startAddress, this);

            // Get the ending address for the trip from the input box
            EditText inputEndAddress = (EditText) findViewById(R.id.tripEndAddress);
            String endAddress = inputEndAddress.getText().toString();

            Log.e("end address", endAddress);

            // Pass the context as well
            tripInstance.setTripEndAddress(endAddress, this);

            // Get the date of the trip from the input box
            EditText inputDate = (EditText) findViewById(R.id.tripStartDate);
            String date = inputDate.getText().toString();
//        tripInstance.setTripStartDate(date);

            // Get the time of the trip from the input box
            EditText inputTime = (EditText) findViewById(R.id.tripStartTime);
            String time = inputTime.getText().toString();
//        tripInstance.setTripStartTime(time);

            // Go to the route select activity
            Intent choseRoute = new Intent(this, RouteSelectActivity.class);
            startActivity(choseRoute);
        } else if (!startLocationGood) {
            Context context = getApplicationContext();
            CharSequence text = "Please supply a trip start location";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if (!endLocationGood) {
            Context context = getApplicationContext();
            CharSequence text = "Please supply a trip end location";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    // Open the settings activity
    // TODO: Might consider moving this function to a class file so that other activities can access
    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
            try{
                String city = "";
                String state = "";

                try{
                    Geocoder gcd = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude(), 1);
                    if (addresses.size() > 0)
                        city = String.valueOf(addresses.get(0).getLocality());
                    state = String.valueOf(addresses.get(0).getAdminArea());

                    EditText tripStartLocation = (EditText) findViewById(R.id.tripStartAddress);
                    tripStartLocation.setText((city + ", " + state));
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

            }catch(NullPointerException npe){

                Toast.makeText(this, "Failed to Connect", Toast.LENGTH_SHORT).show();

                // switch on location service intent
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
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
