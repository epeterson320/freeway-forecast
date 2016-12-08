package co.ericp.freewayforecast

import co.ericp.freewayforecast.routes.Leg
import co.ericp.freewayforecast.routes.Route
import co.ericp.freewayforecast.weather.WeatherPoint
import co.ericp.freewayforecast.weather.WeatherSource
import io.reactivex.Observable

class RouteForecastSourceImpl(val weatherSource: WeatherSource,
                              val calculator: DistanceCalculator
) : RouteForecastSource {

    val MAX_TIME_BETWEEN_POINTS = 60L * 60L * 1000L // 1 hour

    override fun getRouteForecasts(
            routes: List<Route>,
            departure: Long): Observable<RouteForecast> {

        return Observable.fromIterable(routes)
                .flatMap { route -> routeToForecast(route) }
    }

    fun routeToForecast(route: Route): Observable<RouteForecast> {
        val points = getPointsAlongRoute(route)
        val pointsWithWeather = getTempsAtPoints(points)
        return pointsWithWeather
                .toList()
                .map { points -> RouteForecast(route, points) }
                .toObservable()
    }

    /**
     * Calculates the appropriate locations and times of weather points
     * along a route. They should be within 1-hr intervals of each other,
     * and there should be a point at the start and end of the route.
     *
     * @return A list of weather points with location and time data but
     *         without weather data.
     */
    fun getPointsAlongRoute(route: Route): List<WeatherPoint> {

        val firstPoint = WeatherPoint(route.origin, route.startTime)

        val initFoldVal = Pair(listOf(firstPoint), route.startTime)

        val (allPts) = route.legs.fold(initFoldVal, { pair, leg ->
            val (prevPts, startTime) = pair
            val pts = getPointsAlongLeg(leg, startTime).drop(1)

            val nextPts = prevPts + pts
            val nextStart = startTime + leg.duration

            Pair(nextPts, nextStart)
        })

        return allPts
    }

    fun getPointsAlongLeg(leg: Leg, startTime: Long): List<WeatherPoint> {
        val steps = leg.steps

        // number of points, not including end
        val numPoints: Int = Math.ceil(
                (leg.duration).toDouble() / MAX_TIME_BETWEEN_POINTS.toDouble()
        ).toInt()

        // time between points, 60 minutes max
        val interval = leg.duration / numPoints

        var timeTillNext = interval

        val points = mutableListOf(leg.start)

        steps.forEach { step ->
            if (step.duration < timeTillNext) {
                timeTillNext -= step.duration
            } else {
                val rate = step.distance / step.duration
                val offset = rate * timeTillNext
                val dInterval = rate * interval
                val newPoints = pointsAlongPolyline(step.polyline, offset, dInterval)
                points.addAll(newPoints)

                val distTillNext = dInterval - ((step.distance - offset) % dInterval)
                timeTillNext = (distTillNext / rate).toLong()
            }

        }

        val weatherPoints = points.mapIndexed { i, point ->
            WeatherPoint(point, startTime + (i * interval))
        }

        return weatherPoints
    }

    /**
     * Return a number of points along a polyline, equally spaced apart and at
     * a given offset from the beginning point of the polyline.
     *
     * @param polyline
     * @param offset the offset from the first point, in meters
     * @param interval the space between points, in meters
     */
    fun pointsAlongPolyline(polyline: List<Location>, offset: Double, interval: Double): List<Location> {
        if (polyline.size <= 1) throw IllegalArgumentException("Polyline must have at least two points")

        var distTillNext = offset

        val lines = polyline.zip(polyline.drop(1))
        val points = mutableListOf<Location>()

        for ((startPt, endPt) in lines) {
            val dist = calculator.dist(startPt, endPt)
            if (dist < distTillNext) {
                distTillNext -= dist
            } else {
                val newPoints = pointsAlongLine(startPt, endPt, distTillNext, interval)
                points.addAll(newPoints)
                distTillNext = interval - ((dist - distTillNext) % interval)
            }

        }

        val dist = lines.sumByDouble { line ->
            calculator.dist(line.first, line.second)
        }
        val numPoints = Math.floor((dist - offset) / interval).toInt() + 1

        if (numPoints - points.size == 1) {
            points.add(polyline.last())
        }

        return points
    }

    fun pointsAlongLine(start: Location, end: Location, offset: Double, interval: Double): List<Location> {
        val dist = calculator.dist(start, end)
        val numPoints = Math.floor((dist - offset) / interval).toInt() + 1

        return (0 until numPoints).map { i ->
            val distFromStart = offset + interval * i
            calculator.travel(start, end, distFromStart)
        }
    }

    /**
     * From a list of weather points with time and location data but without
     * weather data, return an observable of weather points with weather data
     * filled in.
     */
    fun getTempsAtPoints(points: List<WeatherPoint>): Observable<WeatherPoint> {
        return Observable.fromIterable(points)
                .flatMap { point ->
                    weatherSource.getForecast(point.location, point.time)
                            .toList()
                            .map { forecastPoints -> extrapolate(point, forecastPoints) }
                            .toObservable()
                }
    }

    /**
     * From a list of weather points at varying times, extrapolate weather for
     * a desired time.
     *
     * @param desired A weather point with the desired time and location set.
     * @param available A list of weather points at the same location with varying times.
     * @return An updated version of the desired point, with temperature and status set.
     */
    fun extrapolate(desired: WeatherPoint, available: List<WeatherPoint>): WeatherPoint {
        val prevPoint = available.findLast { forecastPoint ->
            forecastPoint.time < desired.time
        }
        val nextPoint = available.find { forecastPoint ->
            forecastPoint.time > desired.time
        }

        if (prevPoint == null || nextPoint == null) {
            return desired.copy(temp = 25.0)
        }

        val status = prevPoint.status
        val rate = (nextPoint.temp - prevPoint.temp) / (nextPoint.time / prevPoint.time)
        val temp = prevPoint.temp + (desired.time - prevPoint.time) * rate

        return desired.copy(temp = temp, status = status)
    }
}
