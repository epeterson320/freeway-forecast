package co.ericp.freewayforecast.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import co.ericp.freewayforecast.R;
import co.ericp.freewayforecast.routeForecast.RouteForecast;
import co.ericp.freewayforecast.WeatherAdapter;
import co.ericp.freewayforecast.State;

/**
 * Trip forecast
 */
public class TripForecastActivity extends Activity {

    protected RouteForecast forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_trip_forecast);

        forecast = State.getForecast();

        ListView weatherList = (ListView) findViewById(R.id.listview);
        WeatherAdapter adapter = new WeatherAdapter(this, forecast.getWeatherPoints());
        weatherList.setAdapter(adapter);
    }
}
