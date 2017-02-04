package co.ericp.freewayforecast.routes

import co.ericp.freewayforecast.LocationQuery
import org.junit.experimental.categories.Category
import co.ericp.freewayforecast.NetworkTests
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test


@Category(NetworkTests::class)
class GoogleMapsRouteSourceTest {
    @Test
    @Throws(Exception::class)
    fun testGetDirections() {
        val apiKey: String = System.getProperty("GOOGLE_API_KEY")
        val routeSource = GoogleMapsRouteSource(apiKey)

        val routes = routeSource.getRoutes(
                LocationQuery.ByName("Sydney, AU"),
                LocationQuery.ByName("Melbourne, AU")).blockingNext()

        assertNotNull(routes)
        assertNotNull(routes.first())
        assertThat(routes.first().legs.first().steps.first().polyline.size, not(0))
        assertEquals("Sydney NSW, Australia", routes.first().legs.first().start.name)
        assertEquals("Melbourne VIC, Australia", routes.first().legs.first().end.name)
    }
}