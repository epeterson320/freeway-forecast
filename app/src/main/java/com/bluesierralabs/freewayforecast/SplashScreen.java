package com.bluesierralabs.freewayforecast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;


public class SplashScreen extends Activity {

    private AutoCompleteTextView startAutoComplete;
    private AutoCompleteTextView endAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Auto complete example from
        // http://www.tutorialspoint.com/android/android_auto_complete.htm
        startAutoComplete = (AutoCompleteTextView) findViewById(R.id.tripStartAddress);
        endAutoComplete = (AutoCompleteTextView) findViewById(R.id.tripEndAddress);

        String[] countries = getResources().getStringArray(R.array.cities_usa);
        ArrayAdapter adapter = new ArrayAdapter (this,android.R.layout.simple_list_item_1,countries);

        // Set the adapter for the start and end trip fields
        startAutoComplete.setAdapter(adapter);
        endAutoComplete.setAdapter(adapter);
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

    public void submitTimesAndPlaces(View view) {
        // Get the starting address for the trip from the input box
        EditText inputStartAddress = (EditText) findViewById(R.id.tripStartAddress);
        String startAddress = inputStartAddress.getText().toString();

        // Get the ending address for the trip from the input box
        EditText inputEndAddress = (EditText) findViewById(R.id.tripEndAddress);
        String endAddress = inputEndAddress.getText().toString();

        // Get the date of the trip from the input box
        EditText inputDate = (EditText) findViewById(R.id.tripStartDate);
        String date = inputDate.getText().toString();

        // Now we want to send the trip info to the trip manager...

        // Go to the route select activity
        Intent choseRoute = new Intent(this, RouteSelectActivity.class);
        startActivity(choseRoute);
    }

    // Open the settings activity
    // TODO: Might consider moving this function to a class file so that other activities can access
    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}
