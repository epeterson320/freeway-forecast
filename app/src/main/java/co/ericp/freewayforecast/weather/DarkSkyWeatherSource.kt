package co.ericp.freewayforecast.weather

import co.ericp.freewayforecast.routeForecast.Location
import com.google.gson.Gson
import com.squareup.okhttp.*
import io.reactivex.Observable
import java.io.IOException

/**
 * A weather source powered by the Dark Sky API.
 *
 * @see <a href="https://darksky.net/dev/docs/forecast">Dark Sky API Docs</a>
 */
class DarkSkyWeatherSource(val apiKey: String) : WeatherSource {
    val host = "api.darksky.net"
    val client = OkHttpClient()
    val gson = Gson()

    override fun getForecast(location: Location,
                             time: Long,
                             until: Long?): Observable<WeatherPoint> {

        val url = getUrl(location, time)
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)

        val resultRx = Observable.create<Response> { emitter ->
            call.enqueue(object : Callback {
                override fun onResponse(response: Response) {
                    emitter.onNext(response)
                    emitter.onComplete()
                }

                override fun onFailure(request: Request, e: IOException) {
                    emitter.onError(e)
                }
            })

            emitter.setCancellable { call.cancel() }
        }

        return resultRx.flatMap { response ->
            val points = responseToForecast(response)
            Observable.fromIterable(points)
        }
    }

    fun getUrl(loc: Location, time: Long? = null): HttpUrl {
        val querySegment =
                if (time == null) "${loc.lat},${loc.lon}"
                else "${loc.lat},${loc.lon},${time / 1000}"

        return HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment("forecast")
                .addPathSegment(apiKey)
                .addPathSegment(querySegment)
                .addQueryParameter("exclude", "minutely,hourly,daily,alerts,flags")
                .addQueryParameter("units", "si")
                .build()
    }

    fun responseToForecast(okResponse: Response): List<WeatherPoint> {
        val dsResponse = gson.fromJson(
                okResponse.body().charStream(),
                DarkSkyResponse::class.java
        )

        val location = Location(dsResponse.latitude, dsResponse.longitude)
        val time = dsResponse.currently.time
        val temp = dsResponse.currently.temperature
        val icon = dsResponse.currently.icon

        val point = WeatherPoint(location, time, temp, icon.toStatus())
        return listOf(point)
    }

    fun String.toStatus(): Status = when (this) {
        "clear-day" -> Status.CLEAR_DAY
        "clear-night" -> Status.CLEAR_NIGHT
        "rain" -> Status.RAIN
        "snow" -> Status.SNOW
        "sleet" -> Status.SLEET
        "wind" -> Status.WIND
        "fog" -> Status.FOG
        "cloudy" -> Status.CLOUDY
        "partly-cloudy-day" -> Status.PARTLY_CLOUDY_DAY
        "partly-cloudy-night" -> Status.PARTLY_CLOUDY_NIGHT
        else -> Status.UNKNOWN
    }
}
