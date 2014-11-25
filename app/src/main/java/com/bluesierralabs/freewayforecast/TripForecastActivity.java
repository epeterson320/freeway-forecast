package com.bluesierralabs.freewayforecast;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TripForecastActivity extends Activity {

    private ListView weatherListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_forecast);

        Weather weather_data[] = new Weather[]
                {
                        new Weather(R.drawable.clouds, "Cloudy", "10:am will be cold", 10),
                        new Weather(R.drawable.clouds, "Showers", "11:00am will be warmer", 14),
                        new Weather(R.drawable.clouds, "Snow", "12 noon something", 15),
                        new Weather(R.drawable.clouds, "Storm", "1 pm windshield wipers", 23),
                        new Weather(R.drawable.clouds, "Sunny", "2pm ahh thats better", 22)
                };

        WeatherAdapter adapter = new WeatherAdapter(this,
                R.layout.weather_item, weather_data);


        weatherListing = (ListView)findViewById(R.id.listview);

//        View header = (View)getLayoutInflater().inflate(R.layout, null);
//        weatherListing.addHeaderView(header);

        weatherListing.setAdapter(adapter);
    }
}
