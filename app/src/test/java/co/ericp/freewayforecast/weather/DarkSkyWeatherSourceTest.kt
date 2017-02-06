package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.BuildConfig
import co.ericp.freewayforecast.NetworkTests
import co.ericp.freewayforecast.routeForecast.Location
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.Assert.*

@Category(NetworkTests::class)
class DarkSkyWeatherSourceTest {
    @Test
    @Throws(Exception::class)
    fun testGetWeather() {
        val apiKey: String = BuildConfig.DARKSKY_API_KEY
        val weatherSource = DarkSkyWeatherSource(apiKey)

        val ny = Location(40.7128, 74.0059, "New York")
        val point = weatherSource.getForecast(ny).blockingFirst()

        assertNotNull(point)
        assertNotNull(point.status)
        assertEquals(point.location.lat, 40.7128, 0.1)
        assertEquals(point.location.lon, 74.0059, 0.1)
        assert(point.temp > -60.0)
        assert(point.temp < 40.0)
    }
}