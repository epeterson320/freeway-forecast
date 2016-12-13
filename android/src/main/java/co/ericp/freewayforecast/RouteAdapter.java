package co.ericp.freewayforecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import co.ericp.freewayforecast.routes.Route;

/**
 * Provides a list of routes to a ListView
 */
public class RouteAdapter extends ArrayAdapter<Route> {
    public RouteAdapter(Context c, Route[] routes){
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
        Route route = getItem(position);
        view.setText(route.getSummary());
        return view;
    }
}
