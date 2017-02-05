package co.ericp.freewayforecast.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import co.ericp.freewayforecast.Constants;
import co.ericp.freewayforecast.routeForecast.GeoCalculator;
import co.ericp.freewayforecast.R;
import co.ericp.freewayforecast.RouteAdapter;
import co.ericp.freewayforecast.routeForecast.RouteForecast;
import co.ericp.freewayforecast.routeForecast.RouteForecastSource;
import co.ericp.freewayforecast.routeForecast.RouteForecastSourceImpl;
import co.ericp.freewayforecast.State;
import co.ericp.freewayforecast.routes.Route;
import co.ericp.freewayforecast.weather.DarkSkyWeatherSource;
import io.reactivex.functions.Consumer;

import java.util.ArrayList;
import java.util.List;

public class RouteSelectActivity extends FragmentActivity implements /* OnMapReadyCallback, */
        AdapterView.OnItemClickListener {

    private static int ZOOM_PADDING = 50;

    private List<Route> mRoutes = State.getRoutes();
    private RouteForecastSource rfSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("RouteSelect", "onCreate Entered");
        setContentView(R.layout.activity_route_select);
        rfSource = new RouteForecastSourceImpl(
            new DarkSkyWeatherSource(getString(R.string.darksky_api_key)),
            GeoCalculator.INSTANCE
        );
        // Get UI Elements
        //SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        ListView list = (ListView) findViewById(R.id.routes_list);
        RouteAdapter routeAdapter = new RouteAdapter(this, (Route[]) mRoutes.toArray());
        list.setAdapter(routeAdapter);
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

    public void onItemClick(AdapterView parent, View v, final int position, long id) {
        Intent intent = getIntent();
        final Route route = State.getRoutes().get(position);
        List<Route> routes = new ArrayList<>(1);
        routes.add(route);

        long departingOnMillis = intent.getLongExtra(Constants.DEPARTING_ON_EXTRA, 0L);

        rfSource.getRouteForecasts(routes, departingOnMillis)
                .subscribe(
                        new Consumer<RouteForecast>() {
                            @Override
                            public void accept(RouteForecast routeForecast) throws Exception {
                                State.setForecast(routeForecast);
                                Intent showWeatherIntent = new Intent(
                                        RouteSelectActivity.this,
                                        TripForecastActivity.class);
                                startActivity(showWeatherIntent);
                            }
                        }
                );
    }

    /* public void onMapReady(GoogleMap map) {
        Route route = mRoutes.get(0);
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
        com.google.maps.model.LatLng directionsNE = mRoutes.get(0).getNeBound();
        com.google.maps.model.LatLng directionsSW = mRoutes.get(0).getSwBound();

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
                                      List<com.google.maps.model.LatLng> directionsPolyline) {

        com.google.maps.model.LatLng startPos = directionsPolyline.get(0);
        com.google.android.gms.maps.model.LatLng startPosCast =
                new com.google.android.gms.maps.model.LatLng(startPos.lat, startPos.lng);
        map.addMarker(new MarkerOptions().position(startPosCast).title("Start"));

        com.google.maps.model.LatLng endPos = directionsPolyline.get(directionsPolyline.size() - 1);
        com.google.android.gms.maps.model.LatLng endPosCast =
                new com.google.android.gms.maps.model.LatLng(endPos.lat, endPos.lng);
        map.addMarker(new MarkerOptions().position(endPosCast).title("End"));

    } */
}