package co.ericp.freewayforecast

import android.content.Context
import co.ericp.freewayforecast.routeForecast.RouteForecastSource
import co.ericp.freewayforecast.routes.RouteSource
import co.ericp.freewayforecast.weather.WeatherSource
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.Mockito.mock

/**
 * Test rule that puts an offline, mock forecast source in the app.
 */
class RouteForecastRule(val context: Context) : TestRule {
    val weatherSource: WeatherSource = mock(WeatherSource::class.java)
    val routeSource: RouteSource = mock(RouteSource::class.java)
    val routeForecastSource: RouteForecastSource = mock(RouteForecastSource::class.java)

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                val application = context.applicationContext as Application
                application.setRouteForecastSource(routeForecastSource)
                application.setWeatherSource(weatherSource)
                application.setRouteSource(routeSource)

                base.evaluate()

                application.setRouteForecastSource(null)
            }
        }
    }
}