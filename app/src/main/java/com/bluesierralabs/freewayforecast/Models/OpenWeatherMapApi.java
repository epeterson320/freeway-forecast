package com.bluesierralabs.freewayforecast.Models;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.bluesierralabs.freewayforecast.Helpers.App;
import com.bluesierralabs.freewayforecast.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by timothy on 1/1/15.
 * based on http://openweathermap.org/weather-conditions
 */

public class OpenWeatherMapApi {

    private Resources resources = App.getContext().getResources();
    private final Map<Integer, String> codeMap;
    {
        codeMap = new HashMap<Integer, String>();

        // Thunderstorm section
        codeMap.put(200, resources.getString(R.string.weather_thunderstorm_light_rain));
        codeMap.put(201, "Thunderstorm with rain");
        codeMap.put(202, "Thunderstorm with heavy rain");
        codeMap.put(210, "Light thunderstorm");
        codeMap.put(211, "Thunderstorm");
        codeMap.put(212, "Heavy thunderstorm");
        codeMap.put(221, "Ragged thunderstorm");
        codeMap.put(230, "Thunderstorm with light drizzle");
        codeMap.put(231, "Thunderstorm with drizzle");
        codeMap.put(232, "Thunderstorm with heavy drizzle");

        // Drizzle Section
        codeMap.put(300, "Light intensity drizzle");
        codeMap.put(301, "Drizzle");
        codeMap.put(302, "Heavy intensity drizzle");
        codeMap.put(310, "Light intensity drizzle rain");
        codeMap.put(311, "Drizzle rain");
        codeMap.put(312, "Heavy intensity drizzle rain");
        codeMap.put(313, "Shower rain and drizzle");
        codeMap.put(314, "Heavy shower rain and drizzle");
        codeMap.put(321, "Shower drizzle");

        // Rain Section
        codeMap.put(500, "Light rain");
        codeMap.put(501, "Moderate rain");
        codeMap.put(502, "Heavy intensity rain");
        codeMap.put(503, "Very heavy rain");
        codeMap.put(504, "Extreme rain");
        codeMap.put(511, "Freezing rain");
        codeMap.put(520, "Light intensity shower rain");
        codeMap.put(521, "Shower rain");
        codeMap.put(522, "Heavy intensity shower rain");
        codeMap.put(531, "Ragged shower rain");

        // Snow
        codeMap.put(600, "Light snow");
        codeMap.put(601, "Snow");
        codeMap.put(602, "Heavy snow");
        codeMap.put(611, "Sleet");
        codeMap.put(612, "Shower sleet");
        codeMap.put(615, "Light rain and snow");
        codeMap.put(616, "Rain and snow");
        codeMap.put(620, "Light shower snow");
        codeMap.put(621, "Shower snow");
        codeMap.put(622, "Heavy shower snow");

        // Atmosphere - Don't know that I want this but a code could be returned
        codeMap.put(701, "Mist");
        codeMap.put(711, "Smoke");
        codeMap.put(721, "Haze");
        codeMap.put(731, "Sand, dust whirls");
        codeMap.put(741, "Fog");
        codeMap.put(751, "Sand");
        codeMap.put(761, "Dust");
        codeMap.put(762, "Volcanic Ash");
        codeMap.put(771, "Squalls");
        codeMap.put(781, "Tornado");

        // Clouds
        codeMap.put(800, "Clear sky");
        codeMap.put(801, "Few clouds");
        codeMap.put(802, "Scattered clouds");
        codeMap.put(803, "Broken clouds");
        codeMap.put(804, "Overcast clouds");

        // Extreme
        codeMap.put(900, "Tornado");
        codeMap.put(901, "Tropical storm");
        codeMap.put(902, "Hurricane");
        codeMap.put(903, "Cold");
        codeMap.put(904, "Hot");
        codeMap.put(905, "Windy");
        codeMap.put(906, "Hail");

        // Additional
        codeMap.put(951, "Calm");
        codeMap.put(952, "Light breeze");
        codeMap.put(953, "Gentle breeze");
        codeMap.put(954, "Moderate breeze");
        codeMap.put(955, "Fresh breeze");
        codeMap.put(956, "Strong breeze");
        codeMap.put(957, "High wind, near gale");
        codeMap.put(958, "Gale");
        codeMap.put(959, "Severe gale");
        codeMap.put(960, "Storm");
        codeMap.put(961, "Violent Storm");
        codeMap.put(962, "Hurricane");
    }
}
