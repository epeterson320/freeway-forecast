package co.ericp.freewayforecast.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import co.ericp.freewayforecast.Constants;
import co.ericp.freewayforecast.LocationQuery;
import co.ericp.freewayforecast.R;
import co.ericp.freewayforecast.State;
import co.ericp.freewayforecast.routes.GoogleMapsRouteSource;
import co.ericp.freewayforecast.routes.Route;
import io.reactivex.functions.Consumer;

import com.google.android.gms.location.places.Place;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends GooglePlayServicesActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    EditText mFrom;
    EditText mDest;
    EditText mDate;
    EditText mTime;
    Button mSubmitButton;
    java.text.DateFormat df;
    java.text.DateFormat tf;
    Calendar mDepartingOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Splash", "App Started");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();

        df = DateFormat.getDateFormat(this);
        tf = DateFormat.getTimeFormat(this);

        setProgressBarIndeterminate(true);

        mFrom = (EditText) findViewById(R.id.tripStartAddress);
        mDest = (EditText) findViewById(R.id.tripEndAddress);
        mDate = (EditText) findViewById(R.id.tripStartDate);
        mTime = (EditText) findViewById(R.id.tripStartTime);
        mSubmitButton = (Button) findViewById(R.id.submitTimesAndPlaces);

        initializeDateTime();

        // Set the default application preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        mDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showDatePickerDialog(v);
            }
        });

        mTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showTimePickerDialog(v);
            }
        });

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
                //Intent intent = new Intent(this, SettingsActivity.class);
                //startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void placeResult(final Place place){
        Log.i("Main", "Got Place" + place.getAddress());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mFrom.getText().toString().equals("")) {
                    mFrom.setText(place.getAddress());
                } else {
                    Log.i("Main", "didn't fill departing, non-empty field");
                }
            }
        });
    }

    public void getRoutes(View view) {
        Log.i("Splash", "Button Clicked");
        String startAddress = mFrom.getText().toString();
        String endAddress = mDest.getText().toString();

        if (startAddress.length() == 0) {
            CharSequence text = "Please supply a trip start location";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
        else if (endAddress.length() == 0) {
            CharSequence text = "Please supply a trip end location";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("Splash", "Preparing to Start next activity");
            setProgressBarIndeterminateVisibility(true);
            mSubmitButton.setEnabled(false);
            new GoogleMapsRouteSource(getString(R.string.maps_api_key))
                    .getRoutes(new LocationQuery.ByName(startAddress),
                    new LocationQuery.ByName(endAddress),
                    mDepartingOn.getTimeInMillis()).toList().subscribe(
                    new Consumer<List<Route>>() {
                        @Override
                        public void accept(List<Route> routes) throws Exception {
                            MainActivity.this.directionsResult(routes);
                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    }
            );
        }
    }

    public void directionsResult(final List<Route> routes) {
        final Activity act = this;
        runOnUiThread(new Runnable(){
            public void run(){
                setProgressBarIndeterminateVisibility(false);
                mSubmitButton.setEnabled(true);
                if (routes.size() == 0) {
                    Toast.makeText(act, R.string.no_routes_found, Toast.LENGTH_LONG).show();
                } else {
                    Intent pickRouteIntent = new Intent(act, RouteSelectActivity.class);
                    //this should really be done with pickRouteIntent.putExtra();
                    State.setRoutes(routes);
                    pickRouteIntent.putExtra(Constants.DEPARTING_ON_EXTRA, mDepartingOn.getTimeInMillis());
                    startActivity(pickRouteIntent);
                }
            }
        });
    }

    public void directionsFail(final Throwable e) {
        final Activity act = this;
        runOnUiThread(new Runnable(){
            public void run(){
                Toast.makeText(act, e.toString(), Toast.LENGTH_LONG).show();
                Log.e("Splash", "Error getting routes", e);
                setProgressBarIndeterminateVisibility(false);
                mSubmitButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Splash", "Connected.  Getting location...");
    }

    public void initializeDateTime() {
        mDepartingOn = Calendar.getInstance();
        mTime.setText(tf.format(new Date(mDepartingOn.getTimeInMillis())));
        mDate.setText(df.format(new Date(mDepartingOn.getTimeInMillis())));
    }

    public void showTimePickerDialog(View v){
        int hour = mDepartingOn.get(Calendar.HOUR_OF_DAY);
        int minute = mDepartingOn.get(Calendar.MINUTE);
        boolean is24Hour = DateFormat.is24HourFormat(this);
        new TimePickerDialog(this, this, hour, minute, is24Hour).show();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mDepartingOn.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mDepartingOn.set(Calendar.MINUTE, minute);
        mTime.setText(tf.format(new Date(mDepartingOn.getTimeInMillis())));
    }

    public void showDatePickerDialog(View v) {
        int year = mDepartingOn.get(Calendar.YEAR);
        int month = mDepartingOn.get(Calendar.MONTH);
        int day = mDepartingOn.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, this, year, month, day).show();
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mDepartingOn.set(Calendar.YEAR, year);
        mDepartingOn.set(Calendar.MONTH, month);
        mDepartingOn.set(Calendar.DAY_OF_MONTH, day);
        mDate.setText(df.format(new Date(mDepartingOn.getTimeInMillis())));
    }
}