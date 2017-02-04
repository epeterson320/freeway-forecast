package co.ericp.freewayforecast

import co.ericp.freewayforecast.routes.Leg
import co.ericp.freewayforecast.routes.Route
import co.ericp.freewayforecast.routes.Step
import co.ericp.freewayforecast.weather.MockWeatherSource
import co.ericp.freewayforecast.weather.WeatherPoint
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import co.ericp.freewayforecast.Location as Loc

class RouteForecastSourceImplTest {
    val minute = 1000L * 60L
    val anyLatLng = Loc(0, 0)
    val anyHtmlInstructions = "Head \u003cb\u003esoutheast\u003cb\u003e..."
    val delta = 1E-6
    val mockWeatherSource = MockWeatherSource()

    @Test fun weatherPointLocations1() {
        // Given two routes and a forecast source
        val r1 = Route(
                Step(60, 0, 0, 1, 0.75, 2, 1),
                Step(60, 2, 1, 3, 0.75, 4, 0))
        val r2 = Route(
                Step(50, 0, 0, 2, 0),
                Step(50, 2, 0, 4, 0))

        val routes = listOf(r1, r2)
        val rfSource = RouteForecastSourceImpl(mockWeatherSource, CartesianCalculator)
        val departing = System.currentTimeMillis()

        // When I get a forecast for those two routes
        val forecastRx = rfSource.getRouteForecasts(routes, departing).test()
        forecastRx.awaitTerminalEvent()
        val forecasts = forecastRx.values()

        // Then I get weather points at the right locations
        val r1pts = forecasts[0].weatherPoints.map(WeatherPoint::location)
        val r2pts = forecasts[1].weatherPoints.map(WeatherPoint::location)

        val r1expPts = listOf(Loc(0, 0), Loc(2, 1), Loc(4, 0))
        val r2expPts = listOf(Loc(0, 0), Loc(2, 0), Loc(4, 0))

        assertCoordsEqual(r1expPts, r1pts)
        assertCoordsEqual(r2expPts, r2pts)
    }


    @Test fun weatherPointLocations2() {
        // Given a route and a forecast source
        val r = Route(
                Step(25, 0, 0, 1, 0),
                Step(30, 1, 0, 2, 0, 3, 0, 4, 0),
                Step(35, 4, 0, 5, 0),
                Step(20, 5, 0, 6, 0),
                Step(15, 6, 0, 7, 0),
                Step(25, 7, 0, 8, 0))

        val routes = listOf(r)
        val departing = System.currentTimeMillis()

        val rfSource = RouteForecastSourceImpl(mockWeatherSource, CartesianCalculator)

        // When I get directions
        val forecastRx = rfSource.getRouteForecasts(routes, departing).test()
        forecastRx.awaitTerminalEvent()

        // Then it returns weather at the right points
        val expPts = listOf(
                Loc(0.0, 0), Loc(3.5, 0),
                Loc(5.5, 0), Loc(8.0, 0))

        val actPts = forecastRx.values()[0].weatherPoints.map(WeatherPoint::location)

        assertCoordsEqual(expPts, actPts)
    }

    @Test fun pointsAlongLegCoords() {
        val route = Route(
                Step(25, 0, 0, 1, 0),
                Step(30, 1, 0, 2, 0, 3, 0, 4, 0),
                Step(35, 4, 0, 5, 0),
                Step(20, 5, 0, 6, 0),
                Step(15, 6, 0, 7, 0),
                Step(25, 7, 0, 8, 0))

        val leg = route.legs[0]
        val rfSource = RouteForecastSourceImpl(mockWeatherSource, CartesianCalculator)

        val act = rfSource.getPointsAlongLeg(leg, route.startTime)
                .map(WeatherPoint::location)
        val exp = listOf(
                Loc(0.0, 0), Loc(3.5, 0),
                Loc(5.5, 0), Loc(8.0, 0))

        assertCoordsEqual(exp, act, delta)
    }

    @Test fun pointsAlongPolyline() {
        val rfSource = RouteForecastSourceImpl(mockWeatherSource, CartesianCalculator)

        // Side of an equilateral right triangle whose hypotenuse is 1
        val s = Math.sqrt(2.0) / 2

        val p1 = Loc(0, 0)
        val p2 = Loc(s, s)
        val p3 = Loc(s, s + 2)
        val p4 = Loc(0, 2 * s + 2)

        val polyline = listOf(p1, p2, p3, p4)

        val act = rfSource.pointsAlongPolyline(polyline, 0.05, 1.0)

        val exp1 = Loc(s * 0.05, s * 0.05)
        val exp2 = Loc(s, s + 0.05)
        val exp3 = Loc(s, s + 1.05)
        val exp4 = Loc(s * 0.95, s + 2 + s * 0.05)

        val exp = listOf(exp1, exp2, exp3, exp4)

        assertCoordsEqual(exp, act, delta)
    }

    @Test fun pointsAlongPolyline2() {
        val rfSource = RouteForecastSourceImpl(mockWeatherSource, CartesianCalculator)
        val polyline = listOf(
                Loc(1, 0), Loc(2, 0),
                Loc(3, 0), Loc(4, 0)
        )

        val act = rfSource.pointsAlongPolyline(polyline, 2.5, 5.0)
        val exp = listOf(Loc(3.5, 0))

        assertCoordsEqual(exp, act, delta)
    }

    @Test fun pointsAlongPolyline3() {
        val rfSource = RouteForecastSourceImpl(mockWeatherSource, CartesianCalculator)

        val p = Step(4, 0, 0, 1, 0.75, 2, 1).polyline

        val p0ToP1 = CartesianCalculator.dist(p[0], p[1])
        val p1ToP2 = CartesianCalculator.dist(p[1], p[2])

        val dist = p0ToP1 + p1ToP2
        val act = rfSource.pointsAlongPolyline(p, dist, dist)
        val exp = listOf(p.last())

        assertCoordsEqual(act, exp)
    }

    @Test fun pointsAlongLine() {
        val rfSource = RouteForecastSourceImpl(mockWeatherSource, CartesianCalculator)

        val start = Loc(0, 0)
        val end = Loc(0, 5)

        val actPoints = rfSource.pointsAlongLine(start, end, 0.5, 1.0)
        val expPoints = listOf(
                Loc(0, 0.5),
                Loc(0, 1.5),
                Loc(0, 2.5),
                Loc(0, 3.5),
                Loc(0, 4.5)
        )

        assertCoordsEqual(expPoints, actPoints, delta)
    }

    @Test fun pointsAlongLine2() {
        val rfSource = RouteForecastSourceImpl(mockWeatherSource, CartesianCalculator)
        val start = Loc(0, 5)
        val end = Loc(0, 7)

        val act = rfSource.pointsAlongLine(start, end, 1.0, 1.0)
        val exp = listOf(Loc(0, 6), Loc(0, 7))

        assertCoordsEqual(exp, act, delta)
    }

    fun assertCoordsEqual(exp: List<Loc>, act: List<Loc>, delta: Double? = null) {
        assertCoordsEqual(null, exp, act, delta)
    }

    fun assertCoordsEqual(message: String?, exp: List<Loc>, act: List<Loc>, delta: Double? = null) {
        val expCoords = exp.map { it.copy(name = null) }.toTypedArray()
        val actCoords = act.map { it.copy(name = null) }.toTypedArray()

        if (delta == null) {
            assertArrayEquals(message, expCoords, actCoords)
        } else {
            val eLatitudes = expCoords.map(Loc::lat).toDoubleArray()
            val aLatitudes = actCoords.map(Loc::lat).toDoubleArray()
            val latMsg: String = (message?.plus(" (lat)")) ?: "latitude"
            assertArrayEquals(latMsg, eLatitudes, aLatitudes, delta)

            val eLongitudes = expCoords.map(Loc::lon).toDoubleArray()
            val aLongitudes = actCoords.map(Loc::lon).toDoubleArray()
            val lonMsg = (message?.plus(" (lon")) ?: "longitude"
            assertArrayEquals(lonMsg, eLongitudes, aLongitudes, delta)
        }
    }

    fun Step(minutes: Long, vararg ptsAry: Number): Step {
        val xPts = ptsAry.filterIndexed { i, d -> i % 2 == 0 }
        val yPts = ptsAry.filterIndexed { i, d -> i % 2 == 1 }
        val pts = xPts.zip(yPts, { pt1, pt2 -> Loc(pt1, pt2) })

        val dist = pts.zip(pts.drop(1))
                .map { pair -> CartesianCalculator.dist(pair.first, pair.second) }
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

    fun Route(vararg steps: Step): Route {
        val now = System.currentTimeMillis()

        val duration = steps.map(Step::duration).sum()

        val leg = Leg(
                Loc(steps.first().start.lat, steps.first().start.lon, "Start"),
                Loc(steps.last().end.lat, steps.last().end.lon, "End"),
                steps.sumByDouble(Step::distance),
                duration,
                now,
                now + duration,
                steps.asList())

        return Route(
                "",
                leg.distance, leg.duration,
                now, now + leg.duration,
                anyLatLng, anyLatLng,
                leg.start, leg.end,
                listOf(leg))
    }

}
