package co.ericp.freewayforecast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.ericp.freewayforecast.weather.WeatherPoint;

import java.util.List;

/**
 * Custom ArrayAdapter for the trip's hour-by-hour weather forecast
 *
 * Created by timothy on 11/24/14.
 */
public class WeatherAdapter extends ArrayAdapter<WeatherPoint> {

    public WeatherAdapter(Context context, List<WeatherPoint> data) {
        super(context, android.R.layout.simple_list_item_1 , data);
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder;

        if(row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.weather_item, parent, false);
            holder = new WeatherHolder(row);
            row.setTag(holder);

        } else {
            holder = (WeatherHolder) row.getTag();
        }

        WeatherPoint point = getItem(position);
        holder.setData(point);

        return row;
    }

    // Weather items that are shown for each hour
    static class WeatherHolder {
        final Context ctx;
        final ImageView imgIcon;              // Weather Icon
        final TextView hourQuickForecast;     // Weather condition in this location
        final TextView hourTimeAndDetail;     // Time and a little more description
        final TextView hourTemp;              // Temperature for that hour

        WeatherHolder(View v) {
            ctx = v.getContext();
            imgIcon = (ImageView) v.findViewById(R.id.hourIcon);
            hourQuickForecast = (TextView) v.findViewById(R.id.hourQuickForecast);
            hourTimeAndDetail = (TextView) v.findViewById(R.id.hourTimeAndDetail);
            hourTemp = (TextView) v.findViewById(R.id.hourTemp);
        }


        void setData(WeatherPoint data) {
            hourQuickForecast.setText(data.getLocation().toString());

            Drawable icon = ctx.getResources().getDrawable(R.drawable.ic_weather_sunny);
            imgIcon.setImageDrawable(icon);
            hourTimeAndDetail.setText(Long.toString(data.getTime()));
            hourTemp.setText(Double.toString(data.getTemp()));
        }
    }
}
