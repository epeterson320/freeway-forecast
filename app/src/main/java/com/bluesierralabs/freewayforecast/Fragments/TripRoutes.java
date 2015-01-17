package com.bluesierralabs.freewayforecast.Fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bluesierralabs.freewayforecast.Helpers.App;
import com.bluesierralabs.freewayforecast.Helpers.BusProvider;
import com.bluesierralabs.freewayforecast.Services.RouteAddedEvent;
import com.bluesierralabs.freewayforecast.TripForecastActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timothy on 1/11/15.
 */
public class TripRoutes extends ListFragment {
    private final List<String> locationEvents = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, locationEvents);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Do your stuff..
        Intent choseRoute = new Intent(App.getContext(), TripForecastActivity.class);
        startActivity(choseRoute);
    }

    @Subscribe public void onRouteAdded(RouteAddedEvent event) {
        locationEvents.add(0, event.toString());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

//    @Subscribe
//    public void onRouteCleared(LocationClearEvent event) {
//        locationEvents.clear();
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }
//    }
}
