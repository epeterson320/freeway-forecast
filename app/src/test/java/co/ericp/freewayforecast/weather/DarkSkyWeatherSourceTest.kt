package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.NetworkTests
import org.junit.Test
import org.junit.experimental.categories.Category


@Category(NetworkTests::class)
class DarkSkyWeatherSourceTest {
    @Test
    @Throws(Exception::class)
    fun testGetWeather() {
        val apiKey: String = System.getProperty("DARKSKY_API_KEY")
        val weatherSource = DarkSkyWeatherSource(apiKey)

        TODO()
    }
}