package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.BuildConfig
import co.ericp.freewayforecast.routeForecast.LocationQuery
import org.junit.experimental.categories.Category
import co.ericp.freewayforecast.NetworkTests
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Test


@Category(NetworkTests::class)
class GoogleMapsRouteSourceTest {
    @Test
    @Throws(Exception::class)
    fun testGetDirections() {
        val apiKey: String = BuildConfig.GOOGLE_API_KEY
        val routeSource = GoogleMapsRouteSource(apiKey)

        val route: Route = routeSource.getRoutes(
                LocationQuery.ByName("Sydney, AU"),
                LocationQuery.ByName("Melbourne, AU")).blockingFirst()

        assertNotNull(route)
        assertThat(route.legs.first().steps.first().polyline.size, not(0))
        assertEquals("Sydney NSW, Australia", route.legs.first().start.name)
        assertEquals("Melbourne VIC 3004, Australia", route.legs.first().end.name)
    }
}