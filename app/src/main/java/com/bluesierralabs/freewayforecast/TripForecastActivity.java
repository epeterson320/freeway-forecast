package com.bluesierralabs.freewayforecast;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.bluesierralabs.freewayforecast.Models.WeatherItem;

/**
 * Trip forecast
 */
public class TripForecastActivity extends Activity {

    private ListView weatherListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_forecast);

        WeatherItem weather_data[] = new WeatherItem[]
        {
            new WeatherItem(R.drawable.clouds, "Cloudy", "10:am will be cold", 10),
            new WeatherItem(R.drawable.clouds, "Showers", "11:00am will be warmer", 14),
            new WeatherItem(R.drawable.clouds, "Snow", "12 noon something", 15),
            new WeatherItem(R.drawable.clouds, "Storm", "1 pm windshield wipers", 23),
            new WeatherItem(R.drawable.clouds, "Sunny", "2pm ahh thats better", 22)
        };

        WeatherAdapter adapter = new WeatherAdapter(this,
                R.layout.weather_item, weather_data);


        weatherListing = (ListView)findViewById(R.id.listview);

//        View header = (View)getLayoutInflater().inflate(R.layout, null);
//        weatherListing.addHeaderView(header);

        weatherListing.setAdapter(adapter);
    }
}
