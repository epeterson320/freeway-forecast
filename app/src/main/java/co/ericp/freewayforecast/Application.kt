package co.ericp.freewayforecast

import co.ericp.freewayforecast.routeForecast.GeoCalculator
import co.ericp.freewayforecast.routeForecast.RouteForecastSource
import co.ericp.freewayforecast.routeForecast.RouteForecastSourceImpl
import co.ericp.freewayforecast.routes.GoogleMapsRouteSource
import co.ericp.freewayforecast.routes.RouteSource
import co.ericp.freewayforecast.weather.DarkSkyWeatherSource
import co.ericp.freewayforecast.weather.WeatherSource

class Application : android.app.Application() {
    private var weatherSource: WeatherSource? = null
    private var routeSource: RouteSource? = null
    private var forecastSource: RouteForecastSource? = null

    fun getWeatherSource(): WeatherSource {
        if (weatherSource == null) {
            weatherSource = DarkSkyWeatherSource(BuildConfig.DARKSKY_API_KEY)
        }
        return weatherSource!!
    }

    fun setWeatherSource(source: WeatherSource?) {
        weatherSource = source
    }

    fun getRouteSource(): RouteSource {
        if (routeSource == null) {
            routeSource = GoogleMapsRouteSource(BuildConfig.GOOGLE_API_KEY)
        }
        return routeSource!!
    }

    fun setRouteSource(source: RouteSource?) {
        routeSource = source
    }

    fun getRouteForecastSource(): RouteForecastSource {
        if (forecastSource == null) {
            forecastSource = RouteForecastSourceImpl(getWeatherSource(), GeoCalculator)
        }
        return forecastSource!!
    }

    fun setRouteForecastSource(source: RouteForecastSource?) {
        forecastSource = source
    }
}
