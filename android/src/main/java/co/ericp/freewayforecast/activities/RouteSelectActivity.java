package co.ericp.freewayforecast.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import co.ericp.freewayforecast.Constants;
import co.ericp.freewayforecast.R;
import co.ericp.freewayforecast.RoutesAdapter;
import co.ericp.freewayforecast.models.Routes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

public class RouteSelectActivity extends FragmentActivity implements OnMapReadyCallback,
        AdapterView.OnItemClickListener {

    private static int ZOOM_PADDING = 50;

    private DirectionsRoute[] mRoutes = Routes.getRoutes();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("RouteSelect", "onCreate Entered");
        setContentView(R.layout.activity_route_select);

        // Get UI Elements
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ListView list = (ListView) findViewById(R.id.routes_list);
        RoutesAdapter routesAdapter = new RoutesAdapter(this, mRoutes);
        list.setAdapter(routesAdapter);
        list.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {
        Intent showWeatherIntent = new Intent(this, TripForecastActivity.class);
        showWeatherIntent.putExtras(getIntent());
        showWeatherIntent.putExtra(Constants.ROUTE_SELECTED_EXTRA, position);
        startActivity(showWeatherIntent);
    }

    public void onMapReady(GoogleMap map) {
        DirectionsRoute route = mRoutes[0];
        List<com.google.android.gms.maps.model.LatLng> mapsPolyline = new ArrayList<>();
        List<com.google.maps.model.LatLng> directionsPolyline = route.overviewPolyline.decodePath();
        PolylineOptions lineOptions;

        setStartEndMarkers(map, directionsPolyline);

        // Traversing through all the routes
        for (int i = 0; i < mRoutes.length; i++) {
            Log.e("RouteSelectActivity", "printing route " + (i + 1));
            route = mRoutes[i];
            lineOptions = new PolylineOptions();

            directionsPolyline = route.overviewPolyline.decodePath();

            for (int j = 0; j < directionsPolyline.size(); j++) {
                com.google.maps.model.LatLng directionsPoint = directionsPolyline.get(j);
                com.google.android.gms.maps.model.LatLng mapPoint =
                        new com.google.android.gms.maps.model.LatLng(directionsPoint.lat, directionsPoint.lng);
                mapsPolyline.add(mapPoint);
            }

            // Adding all the mapsPolyline in the route to LineOptions
            lineOptions.addAll(mapsPolyline);
            lineOptions.width(7);

            switch (i) {
                case 0:
                    lineOptions.color(Color.parseColor("#009933")); // Contrast green
                    break;
                case 1:
                    lineOptions.color(Color.BLUE);
                    break;
                case 2:
                    lineOptions.color(Color.RED);
                    break;
                default:
                    lineOptions.color(Color.parseColor("#8000FF")); // Purple
                    break;
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);

            // Add the markers to the map
            // TODO: Add this back in later when I return to the marker accuracy
//            for (int i = 0; i < mTrip.getWeatherItems().size(); i++) {
//                mMap.addMarker(new MarkerOptions().position(mTrip.getWeatherItems().get(i).getLocation()));
//            }

            setAndZoomToBounds(map);

        }
    }

    protected void setAndZoomToBounds(GoogleMap map){
        // Set the bounds using the bounds for the first route.
        com.google.maps.model.LatLng directionsNE = mRoutes[0].bounds.northeast;
        com.google.maps.model.LatLng directionsSW = mRoutes[0].bounds.southwest;

        com.google.android.gms.maps.model.LatLng mapsNE = new LatLng(directionsNE.lat, directionsNE.lng);
        com.google.android.gms.maps.model.LatLng mapsSW = new LatLng(directionsSW.lat, directionsSW.lng);

        LatLngBounds mapBounds = new LatLngBounds.Builder()
            .include(mapsNE)
            .include(mapsSW)
            .build();

        // Zoom the mMap to show only the route
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, ZOOM_PADDING));
    }

    protected void setStartEndMarkers(GoogleMap map,
                                      List<com.google.maps.model.LatLng> directionsPolyline){

        com.google.maps.model.LatLng startPos = directionsPolyline.get(0);
        com.google.android.gms.maps.model.LatLng startPosCast =
                new com.google.android.gms.maps.model.LatLng(startPos.lat, startPos.lng);
        map.addMarker(new MarkerOptions().position(startPosCast).title("Start"));

        com.google.maps.model.LatLng endPos = directionsPolyline.get(directionsPolyline.size() - 1);
        com.google.android.gms.maps.model.LatLng endPosCast =
                new com.google.android.gms.maps.model.LatLng(endPos.lat, endPos.lng);
        map.addMarker(new MarkerOptions().position(endPosCast).title("End"));

    }
}