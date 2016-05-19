package co.ericp.freewayforecast

import co.ericp.freewayforecast.routes.Leg
import co.ericp.freewayforecast.routes.Route
import co.ericp.freewayforecast.routes.RouteSource
import co.ericp.freewayforecast.routes.Step
import co.ericp.freewayforecast.weather.Forecast
import co.ericp.freewayforecast.weather.WeatherPoint
import co.ericp.freewayforecast.weather.WeatherSource
import co.ericp.freewayforecast.LocationQuery.ByName
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.Matchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rx.Single

class RouteForecastSourceImplTest {
    val minute = 1000L * 60L
    val anyLatLng = LatLon(0.0, 0.0)
    val anyHtmlInstructions = "Head \u003cb\u003esoutheast\u003cb\u003e"

    @Test fun weatherPointLocations1() {
        // Given a route source that returns two routes

        val r1 = route(
                step(60, 0.00, 0.00, 1.00, 0.75, 2.00, 1.00),
                step(60, 2.00, 1.00, 3.00, 0.75, 4.00, 0.00))

        val r2 = route(
                step(50, 0.00, 0.00, 2.00, 0.00),
                step(50, 2.00, 0.00, 4.00, 0.00))

        val mockRouteSource = mock(RouteSource::class.java)
        `when`(mockRouteSource.getRoutes(ByName("Start"), ByName("End")))
                .thenReturn(Single.just(listOf(r1, r2)))

        val rfSource: RouteForecastSource =
                RouteForecastSourceImpl(mockRouteSource, mockWeatherSource)

        // When I get directions

        val forecast = rfSource.getRouteForecast(
                ByName("Start"), ByName("End"),
                System.currentTimeMillis()).toBlocking().value()

        // Then I get weather points at the right locations
        val r1pts = forecast[0].weatherPoints.map(WeatherPoint::coords)
        val r2pts = forecast[1].weatherPoints.map(WeatherPoint::coords)

        val r1expPts = listOf(LatLon(0.0, 0.0), LatLon(2.0, 1.0), LatLon(4.0, 0.0))
        val r2expPts = listOf(LatLon(0.0, 0.0), LatLon(2.0, 0.0), LatLon(4.0, 0.0))

        assertThat(r1pts, `is`(r1expPts))
        assertThat(r2pts, `is`(r2expPts))
    }


    @Test fun weatherPointLocations2() {
        // Given a route source

        val r = route(
                step(25, 0.0, 0.0, 1.0, 0.0),
                step(30, 1.0, 0.0, 2.0, 0.0, 3.0, 0.0, 4.0, 0.0),
                step(35, 4.0, 0.0, 5.0, 0.0),
                step(20, 5.0, 0.0, 6.0, 0.0),
                step(15, 6.0, 0.0, 7.0, 0.0),
                step(25, 7.0, 0.0, 8.0, 0.0))

        val mockRouteSource = mock(RouteSource::class.java)
        `when`(mockRouteSource.getRoutes(ByName("Start"), ByName("End")))
                .thenReturn(Single.just(listOf(r)))

        val rfSource = RouteForecastSourceImpl(mockRouteSource, mockWeatherSource)

        // When I get directions
        val forecast = rfSource.getRouteForecast(
                ByName("Start"), ByName("End"),
                System.currentTimeMillis()).toBlocking().value();

        // Then it returns weather at the right points
        val expPts = listOf(LatLon(0.0, 0.0), LatLon(3.5, 0.0),
                LatLon(5.5, 0.0), LatLon(8.0, 0.0))

        val pts = forecast[0].weatherPoints.map(WeatherPoint::coords)

        assertThat(pts, `is`(expPts))
    }

    fun step(minutes: Long, vararg ptsAry: Double): Step {
        val xPts = ptsAry.filterIndexed { i, d -> i % 2 == 0 }
        val yPts = ptsAry.filterIndexed { i, d -> i % 2 == 1 }
        val pts = xPts.zip(yPts, { x, y -> LatLon(x, y) })

        val cartesianDist = pts.zip(pts.drop(1)).map { pair ->
            val (p1, p2) = pair
            val dx = p1.lon - p2.lon
            val dy = p1.lat - p2.lat
            Math.sqrt((dx * dx) + (dy * dy))
        }.sum()

        return Step(
                anyHtmlInstructions,
                cartesianDist,
                minutes * minute,
                pts.first(),
                pts.last(),
                pts
        )
    }

    fun route(vararg steps: Step): Route {
        val now = System.currentTimeMillis()

        val leg = Leg(Location("Start", steps.first().start),
                Location("End", steps.last().end),
                steps.sumByDouble(Step::distance),
                steps.map(Step::duration).sum(),
                steps.asList())

        return Route(
                leg.distance, leg.duration,
                now, now + leg.duration,
                anyLatLng, anyLatLng,
                leg.start, leg.end,
                listOf(leg))
    }

    val mockWeatherSource = object : WeatherSource {
        override fun getForecast(
                coords: LatLon,
                time: Long): Single<Forecast> {

            val points = listOf(
                    WeatherPoint(coords, time + 0 * minute, 20.0, 0),
                    WeatherPoint(coords, time + 60 * minute, 20.0, 0),
                    WeatherPoint(coords, time + 120 * minute, 20.0, 0))

            val forecast = Forecast(coords, points,
                    points.first().time, points.last().time)

            return Single.just(forecast)
        }
    }
}
