package co.ericp.freewayforecast

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) return

            val inputTripFragment = PickTripFragment.newInstance("i", "j")

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, inputTripFragment).commit()
        }
    }

    fun showChooseRoutes() {
        val chooseRouteFragment = PickTripFragment.newInstance("i", "j")

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, chooseRouteFragment)
                .addToBackStack(null)
                .commit()
    }
}
