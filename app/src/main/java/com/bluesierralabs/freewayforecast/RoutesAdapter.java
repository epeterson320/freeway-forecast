package com.bluesierralabs.freewayforecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.maps.model.DirectionsRoute;

/**
 * Provides a list of routes to a ListView
 */
public class RoutesAdapter extends ArrayAdapter<DirectionsRoute> {
    public RoutesAdapter(Context c, DirectionsRoute[] routes){
        super(c, android.R.layout.simple_list_item_1, routes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        TextView view;

        if (convertView == null) {
            view = (TextView) LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        } else {
            view = (TextView) convertView;
        }
        DirectionsRoute route = getItem(position);
        view.setText(route.summary);
        return view;
    }
}
