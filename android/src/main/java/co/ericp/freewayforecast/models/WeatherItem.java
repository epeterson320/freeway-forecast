package co.ericp.freewayforecast.models;

/**
 * Model to hold weather at a point on a route
 */
public class WeatherItem {

    public WeatherItem(String description, String icon, String temperature, String time) {
        this.description = description;
        this.icon = icon;
        this.temperature = temperature;
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String description;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String icon;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String temperature;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String time;

}
