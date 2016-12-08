package co.ericp.freewayforecast.weather;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO corresponding to a response from the Dark Sky API. Used for
 * deserializing with Gson.
 *
 * @see <a href="https://darksky.net/dev/docs/response">Dark Sky Response Format</a>
 */
class DarkSkyResponse {
    double latitude;
    double longitude;
    String timezone;
    DataPoint currently;
    DataBlock minutely;
    DataBlock hourly;
    DataBlock daily;
    Alert[] alerts;
    Flags flags;

    static class DataPoint {
        double apparentTemperature;
        double apparentTemperatureMax;
        long apparentTemperatureMaxTime;
        double apparentTemperatureMin;
        long apparentTemperatureMinTime;
        double cloudCover;
        double dewPoint;
        double humidity;
        String icon;
        double moonPhase;
        double nearestStormBearing;
        double nearestStormDistance;
        double ozone;
        double precipAccumulation;
        double precipIntensity;
        double precipIntensityMax;
        long precipIntensityMaxTime;
        double precipProbability;
        String precipType;
        double pressure;
        String summary;
        long sunriseTime;
        long sunsetTime;
        double temperature;
        double temperatureMax;
        long temperatureMaxTime;
        double temperatureMin;
        long temperatureMinTime;
        long time;
        double visibility;
        double windBearing;
        double windSpeed;
    }

    static class DataBlock {
        DataPoint[] data;
        String summary;
        String icon;
    }

    static class Alert {
        String description;
        long expires;
        String title;
        String uri;
    }

    static class Flags {
        @SerializedName("darksky-unavailable") String unavailable;
        @SerializedName("metno-license") String metNoLicense;
        String[] sources;
        String units;
    }
}