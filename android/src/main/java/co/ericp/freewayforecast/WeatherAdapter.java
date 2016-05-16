package co.ericp.freewayforecast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.ericp.freewayforecast.R;

import co.ericp.freewayforecast.models.WeatherItem;

import java.util.List;

/**
 * Custom ArrayAdapter for the trip's hour-by-hour weather forecast
 *
 * Created by timothy on 11/24/14.
 */
public class WeatherAdapter extends ArrayAdapter<WeatherItem>{

    public WeatherAdapter(Context context, List<WeatherItem> data) {
        super(context, android.R.layout.simple_list_item_1 , data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.weather_item, parent, false);

            holder = new WeatherHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.hourIcon);
            holder.hourQuickForecast = (TextView)row.findViewById(R.id.hourQuickForecast);
            holder.hourTimeAndDetail = (TextView)row.findViewById(R.id.hourTimeAndDetail);
            holder.hourTemp = (TextView)row.findViewById(R.id.hourTemp);

            row.setTag(holder);
        }
        else
        {
            holder = (WeatherHolder)row.getTag();
        }

        WeatherItem weather = getItem(position);
        holder.hourQuickForecast.setText(weather.getDescription());
        Drawable icon =
            getContext().getResources().getDrawable(R.drawable.ic_weather_sunny);
        holder.imgIcon.setImageDrawable(icon);
        holder.hourTimeAndDetail.setText(weather.getTime());
        holder.hourTemp.setText(weather.getTemperature());

        return row;
    }

    // Weather items that are shown for each hour
    static class WeatherHolder
    {
        ImageView imgIcon;              // Weather Icon
        TextView hourQuickForecast;     // Weather condition in this location
        TextView hourTimeAndDetail;     // Time and a little more description
        TextView hourTemp;              // Temperature for that hour
    }
}
