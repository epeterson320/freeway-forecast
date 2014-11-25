package com.bluesierralabs.freewayforecast;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom ArrayAdapter for the trip's hour-by-hour weather forecast
 *
 * Created by timothy on 11/24/14.
 */
public class WeatherAdapter extends ArrayAdapter<Weather>{
    Context context;
    int layoutResourceId;
    Weather data[] = null;

    public WeatherAdapter(Context context, int layoutResourceId, Weather[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

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

        Weather weather = data[position];
        holder.hourQuickForecast.setText(weather.title);
        holder.imgIcon.setImageResource(weather.icon);
        holder.hourTimeAndDetail.setText(weather.detail);
        holder.hourTemp.setText(weather.temp);

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
