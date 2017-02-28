package co.ericp.freewayforecast;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import co.ericp.freewayforecast.routeForecast.LocationQuery;
import co.ericp.freewayforecast.routeForecast.RouteForecast;
import co.ericp.freewayforecast.routeForecast.RouteForecastSource;
import co.ericp.freewayforecast.routes.Route;
import co.ericp.freewayforecast.routes.RouteSource;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link PickTripFragment.OnForecastListener} interface to handle interaction events. Use the
 * {@link PickTripFragment#newInstance} factory method to create an instance of this fragment.
 */
public class PickTripFragment extends Fragment {
    private OnForecastListener mListener;

    private boolean mLoading = false;

    private View mLoadingIndicator;
    private EditText mOriginField;
    private EditText mDestinationField;
    private RouteSource mRouteSource;
    private RouteForecastSource mRouteForecastSource;
    private CompositeDisposable allDisposables = new CompositeDisposable();

    public PickTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @return A new instance of fragment BlankFragment.
     */
    public static PickTripFragment newInstance() {
        return new PickTripFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_trip, container, false);
        mLoadingIndicator = view.findViewById(R.id.loading_indicator);
        mOriginField = (EditText) view.findViewById(R.id.input_origin);
        mDestinationField = (EditText) view.findViewById(R.id.input_destination);

        view.findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOK(v);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mListener = (OnForecastListener) context;
            mRouteSource = ((MainActivity) context).getRouteSource();
            mRouteForecastSource = ((MainActivity) context).getRouteForecastSource();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        allDisposables.dispose();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other
     * fragments contained in that activity. <p> See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating
     * with Other Fragments</a> for more information.
     */
    public interface OnForecastListener {
        void onNextForecast(RouteForecast forecast);
    }

    public void onClickOK(View v) {
        if (mLoading) return;

        mLoading = true;
        mLoadingIndicator.setVisibility(View.VISIBLE);

        String origin = mOriginField.getText().toString();
        String destination = mDestinationField.getText().toString();
        final long date = System.currentTimeMillis() + (1000 * 60 * 60);

        Disposable disposeRoutes = mRouteSource.getRoutes(
                new LocationQuery.ByName(origin),
                new LocationQuery.ByName(destination),
                date)
                .toList()
                .flatMap(new Function<List<Route>, SingleSource<List<RouteForecast>>>() {
                    @Override
                    public SingleSource<List<RouteForecast>> apply(List<Route> routes) throws Exception {
                        return mRouteForecastSource.getRouteForecasts(routes, date).toList();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mLoading = false;
                    }
                })
                .subscribe(
                new Consumer<List<RouteForecast>>() {
                    @Override
                    public void accept(List<RouteForecast> routes) throws Exception {
                        onGotForecasts(routes);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        // Snackbar the error.
                    }
                }
        );

        allDisposables.add(disposeRoutes);
    }

    public void onGotForecasts(List<RouteForecast> forecasts) {
        if (mListener == null) return;

        for (RouteForecast forecast : forecasts) {
            mListener.onNextForecast(forecast);
        }
    }
}
