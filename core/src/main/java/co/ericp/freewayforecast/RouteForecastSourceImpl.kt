package co.ericp.freewayforecast

import co.ericp.freewayforecast.routes.Leg
import co.ericp.freewayforecast.routes.Route
import co.ericp.freewayforecast.weather.WeatherPoint
import co.ericp.freewayforecast.weather.WeatherSource
import rx.Observable

class RouteForecastSourceImpl(
        val weatherSource: WeatherSource
) : RouteForecastSource {

    val MAX_TIME_BETWEEN_POINTS = 60 * 1000;

    override fun getRouteForecasts(
            routes: List<Route>,
            departure: Long): Observable<RouteForecast> {
        return Observable.empty()
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

        val steps = route.legs.flatMap(Leg::steps)

        // number of points, not including start or end
        val numPoints: Int = Math.ceil(
                route.duration.toDouble() / MAX_TIME_BETWEEN_POINTS
        ).toInt() - 1

        // time between points, 60 minutes max
        val interval: Long = route.duration / (numPoints + 1)

        var timeTillNext: Long = interval

        steps.forEach { step ->
            if (step.duration < timeTillNext) {
                timeTillNext -= step.duration

            } else {
                val rate = step.distance / step.duration
                var prevPt = step.polyline.first()


                for (curPt in step.polyline.drop(1)) {
                    val duration = Location.dist(prevPt, curPt) / rate
                    if (duration < timeTillNext) {
                        timeTillNext -= duration;
                    } else {

                    }

                    prevPt = curPt
                }
            }

        }

        return emptyList()
    }

    /**
     * From a list of weather points with time and location data but without
     * weather data, return an observable of weather points with weather data
     * filled in.
     */
    fun getTempsAtPoints(points: List<WeatherPoint>): Observable<WeatherPoint> {
        return Observable.from(points)
                .flatMap { point ->
                    weatherSource.getForecast(point.location, point.time)
                            .toList()
                            .map { forecastPoints -> interpolate(point, forecastPoints) }
                }
    }

    fun interpolate(desired: WeatherPoint, available: List<WeatherPoint>): WeatherPoint {
        val prevPoint = available.findLast { forecastPoint ->
            forecastPoint.time < desired.time
        }
        val nextPoint = available.find { forecastPoint ->
            forecastPoint.time > desired.time
        }

        if (prevPoint == null || nextPoint == null) {
            return desired.copy(temp = 25.0, status = -1)
        }

        val status = prevPoint.status
        val rate = (nextPoint.temp - prevPoint.temp) / (nextPoint.time / prevPoint.time)
        val temp = prevPoint.temp
        + (desired.time - prevPoint.time) * rate

        return desired.copy(temp = temp, status = status)
    }
}
