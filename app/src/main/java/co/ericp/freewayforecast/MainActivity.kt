package co.ericp.freewayforecast

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import co.ericp.freewayforecast.routeForecast.GeoCalculator
import co.ericp.freewayforecast.routeForecast.RouteForecast
import co.ericp.freewayforecast.routeForecast.RouteForecastSource
import co.ericp.freewayforecast.routeForecast.RouteForecastSourceImpl
import co.ericp.freewayforecast.routes.GoogleMapsRouteSource
import co.ericp.freewayforecast.routes.RouteSource
import co.ericp.freewayforecast.weather.DarkSkyWeatherSource
import co.ericp.freewayforecast.weather.WeatherSource

class MainActivity : AppCompatActivity(), PickTripFragment.OnForecastListener {

    var routeSource: RouteSource = GoogleMapsRouteSource(BuildConfig.GOOGLE_API_KEY)
    var weatherSource: WeatherSource = DarkSkyWeatherSource(BuildConfig.DARKSKY_API_KEY)
    var routeForecastSource: RouteForecastSource =
            RouteForecastSourceImpl(weatherSource, GeoCalculator)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) return

            val inputTripFragment = PickTripFragment.newInstance()

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, inputTripFragment).commit()
        }
    }

    override fun onNextForecast(forecast: RouteForecast) {
        val viewTripFragment = ViewTripFragment.newInstance("i", "j")

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewTripFragment)
                .addToBackStack(null)
                .commit()
    }
}
