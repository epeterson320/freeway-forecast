package co.ericp.freewayforecast

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import co.ericp.freewayforecast.routeForecast.RouteForecast

class MainActivity : AppCompatActivity(), PickTripFragment.OnForecastListener {

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
        // Ignore for now. In large screen mode, should add a route forecast
        // to the map on the screen.
    }

    override fun onChooseForecast(forecast: RouteForecast) {
        val viewTripFragment = ViewTripFragment.newInstance("i", "j")

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewTripFragment)
                .addToBackStack(null)
                .commit()
    }
}
