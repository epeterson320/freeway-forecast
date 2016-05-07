package co.ericp.freewayforecast.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;

import co.ericp.freewayforecast.Constants;
import com.bluesierralabs.freewayforecast.R;
import co.ericp.freewayforecast.TripWeatherTask;
import co.ericp.freewayforecast.WeatherAdapter;
import co.ericp.freewayforecast.models.Routes;
import co.ericp.freewayforecast.models.WeatherItem;
import com.google.maps.model.DirectionsRoute;

import java.util.Calendar;
import java.util.List;

/**
 * Trip forecast
 */
public class TripForecastActivity extends Activity implements TripWeatherTask.TripWeatherCallbacks {

    protected DirectionsRoute mRoute;
    protected Calendar mDepartingOn = Calendar.getInstance();
    protected TripWeatherTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_trip_forecast);

        Intent intent = getIntent();
        mRoute = Routes.getRoutes()[intent.getIntExtra(Constants.ROUTE_SELECTED_EXTRA, 0)];

        long departingOnMillis = intent.getLongExtra(Constants.DEPARTING_ON_EXTRA, 0L);
        mDepartingOn.setTimeInMillis(departingOnMillis);

        Log.i("Entering forecast with", "" + mRoute.legs[0].steps.length + " steps");
    }

    @Override
    protected void onStart() {
        super.onStart();
        setProgress(0);
        mTask = new TripWeatherTask(mRoute, mDepartingOn, this);
        mTask.execute();
    }

    protected void onStop(){
        mTask.cancel(true);
        super.onStop();
    }

    public void onTripWeatherComplete(List<WeatherItem> weatherItems){
        setProgress(10000);
        WeatherAdapter adapter = new WeatherAdapter(this, weatherItems);
        ListView weatherList = (ListView)findViewById(R.id.listview);
        weatherList.setAdapter(adapter);
    }

    public void onTripWeatherProgress(Integer progress){
        setProgress(progress);
    }

}
