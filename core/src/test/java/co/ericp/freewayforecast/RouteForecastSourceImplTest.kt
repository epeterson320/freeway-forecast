package co.ericp.freewayforecast

import co.ericp.freewayforecast.routes.Leg
import co.ericp.freewayforecast.routes.Route
import co.ericp.freewayforecast.routes.Step
import co.ericp.freewayforecast.weather.WeatherPoint
import co.ericp.freewayforecast.weather.WeatherSource
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import rx.Observable

class RouteForecastSourceImplTest {
    val minute = 1000L * 60L
    val anyLatLng = Location(0.0, 0.0)
    val anyHtmlInstructions = "Head \u003cb\u003esoutheast\u003cb\u003e"

    @Test fun weatherPointLocations1() {
        // Given two routes and a forecast source
        val r1 = route(
                step(60, 0.00, 0.00, 1.00, 0.75, 2.00, 1.00),
                step(60, 2.00, 1.00, 3.00, 0.75, 4.00, 0.00))
        val r2 = route(
                step(50, 0.00, 0.00, 2.00, 0.00),
                step(50, 2.00, 0.00, 4.00, 0.00))

        val routes = listOf(r1, r2)
        val rfSource = RouteForecastSourceImpl(mockWeatherSource)
        val departing = System.currentTimeMillis()

        // When I get a forecast for those two routes
        val forecast = rfSource.getRouteForecasts(routes, departing)
            .toBlocking()
            .toIterable()
            .iterator()

        // Then I get weather points at the right locations
        val r1pts = forecast.next().weatherPoints.map(WeatherPoint::location)
        val r2pts = forecast.next().weatherPoints.map(WeatherPoint::location)

        val r1expPts = listOf(Location(0.0, 0.0), Location(2.0, 1.0), Location(4.0, 0.0))
        val r2expPts = listOf(Location(0.0, 0.0), Location(2.0, 0.0), Location(4.0, 0.0))

        assertThat(r1pts, `is`(r1expPts))
        assertThat(r2pts, `is`(r2expPts))
    }


    @Test fun weatherPointLocations2() {
        // Given a route and a forecast source
        val r = route(
                step(25, 0.0, 0.0, 1.0, 0.0),
                step(30, 1.0, 0.0, 2.0, 0.0, 3.0, 0.0, 4.0, 0.0),
                step(35, 4.0, 0.0, 5.0, 0.0),
                step(20, 5.0, 0.0, 6.0, 0.0),
                step(15, 6.0, 0.0, 7.0, 0.0),
                step(25, 7.0, 0.0, 8.0, 0.0))

        val routes = listOf(r)
        val departing = System.currentTimeMillis()

        val rfSource = RouteForecastSourceImpl(mockWeatherSource)

        // When I get directions
        val forecast = rfSource.getRouteForecasts(routes, departing)
                .toBlocking()
                .toIterable()
                .iterator()
                .next()

        // Then it returns weather at the right points
        val expPts = listOf(
                Location(0.0, 0.0), Location(3.5, 0.0),
                Location(5.5, 0.0), Location(8.0, 0.0))

        val actPts = forecast.weatherPoints.map(WeatherPoint::location)
        assertThat(actPts, `is`(expPts))
    }

    fun step(minutes: Long, vararg ptsAry: Double): Step {
        val xPts = ptsAry.filterIndexed { i, d -> i % 2 == 0 }
        val yPts = ptsAry.filterIndexed { i, d -> i % 2 == 1 }
        val pts = xPts.zip(yPts, { pt1, pt2 -> Location(pt1, pt2) })

        val dist = pts.zip(pts.drop(1))
                .map { pair -> Location.dist(pair.first, pair.second) }
                .sum()

        return Step(
                anyHtmlInstructions,
                dist,
                minutes * minute,
                pts.first(),
                pts.last(),
                pts
        )
    }

    fun route(vararg steps: Step): Route {
        val now = System.currentTimeMillis()

        val leg = Leg(
                Location(steps.first().start.lat, steps.first().start.lon, "Start"),
                Location(steps.last().end.lat, steps.last().end.lon, "End"),
                steps.fold(0L, { acc, step -> step.distance + acc }),
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
                location: Location,
                time: Long,
                until: Long?): Observable<WeatherPoint> {

            val points = listOf(
                    WeatherPoint(location, time + 0 * minute, 20.0, 0),
                    WeatherPoint(location, time + 60 * minute, 20.0, 0),
                    WeatherPoint(location, time + 120 * minute, 20.0, 0))

            return Observable.from(points)
        }
    }
}
