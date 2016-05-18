package co.ericp.freewayforecast

import co.ericp.freewayforecast.routes.*
import co.ericp.freewayforecast.weather.Forecast
import co.ericp.freewayforecast.weather.WeatherPoint
import co.ericp.freewayforecast.weather.WeatherSource
import org.junit.Test
import rx.Single

import org.junit.Assert.assertThat
import org.junit.Assert.assertArrayEquals

class RouteForecastSourceImplTest {
    val minute = 1000L * 60L
    val anyLatLng = LatLng(0.0, 0.0)
    val anyHtmlInstructions = "Head \u003cb\u003esoutheast\u003cb\u003e"

    @Test fun weatherPointLocations1() {
        // Given a route source that returns two routes

        val r1 = route(
                step(60, 0.00, 0.00, 1.00, 0.75, 2.00, 1.00),
                step(60, 2.00, 1.00, 3.00, 0.75, 4.00, 0.00))

        val r2 = route(
                step(50, 0.00, 0.00, 2.00, 0.00),
                step(50, 2.00, 0.00, 4.00, 0.00))

        val rSource = object : RouteSource {
            override fun getRoutes(
                    origin: LocationQuery,
                    destination: LocationQuery): Single<List<Route>> {

                return Single.just(listOf(r1, r2));
            }
        }

        val wSource = object : WeatherSource {
            override fun getForecast(
                    coords: LatLng,
                    time: Long): Single<Forecast> {
                //TODO
                return Single.error(Exception("Todo"))
            }
        }

        val rfSource: RouteForecastSource =
                RouteForecastSourceImpl(rSource, wSource)

        // When I get directions

        val forecast = rfSource.getRouteForecast(
                LocationQuery.ByName("Start"),
                LocationQuery.ByName("End"),
                System.currentTimeMillis()).toBlocking().value()

        // Then I get weather points at the right locations
        val r1pts = forecast.get(0).weatherPoints
        val r2pts = forecast.get(1).weatherPoints

        val r1expPts =
                listOf(LatLng(0.0, 0.0), LatLng(2.0, 1.0), LatLng(4.0, 0.0))
        val r2expPts =
                listOf(LatLng(0.0, 0.0), LatLng(2.0, 0.0), LatLng(4.0, 0.0))

        // TODO get these the same type
        //assertArrayEquals(r1expPts, r1pts)
        //assertArrayEquals(r2expPts, r2pts)
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

        // When I get directions


        // Then it returns weather at the right points
        val expPts = listOf(
                LatLng(0.0, 0.0), LatLng(3.5, 0.0),
                LatLng(5.5, 0.0), LatLng(8.0, 0.0))
    }

    fun step(minutes: Long, vararg ptsAry: Double): Step {
        val xPts = ptsAry.filterIndexed { i, d -> i % 2 == 0 }
        val yPts = ptsAry.filterIndexed { i, d -> i % 2 == 1 }
        val pts = xPts.zip(yPts, { x, y -> LatLng(x, y) })

        val cartesianDist = pts.zip(pts.drop(1)).map { pair ->
            val (p1, p2) = pair
            val dx = p1.lng - p2.lng
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
                steps.sumByDouble { it.distance },
                steps.map { it.duration }.sum(),
                steps.asList())

        return Route(leg.distance, leg.duration, now, now + leg.duration,
                anyLatLng, anyLatLng, leg.start, leg.end, listOf(leg))
    }
}
