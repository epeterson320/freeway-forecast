package co.ericp.freewayforecast

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView

import co.ericp.freewayforecast.routeForecast.LocationQuery
import co.ericp.freewayforecast.routeForecast.RouteForecast
import co.ericp.freewayforecast.routeForecast.RouteForecastSource
import co.ericp.freewayforecast.routes.RouteSource
import io.reactivex.disposables.CompositeDisposable


/**
 * A simple [Fragment] subclass. Activities that contain this fragment must implement the
 * [PickTripFragment.OnForecastListener] interface to handle interaction events. Use the
 * [PickTripFragment.newInstance] factory method to create an instance of this fragment.
 */
class PickTripFragment : Fragment() {
    companion object {
        val TAG = "PickTripFragment"

        /**
         * Use this factory method to create a new instance of this fragment using the provided
         * parameters.

         * @return A new instance of fragment BlankFragment.
         */
        fun newInstance(): PickTripFragment {
            return PickTripFragment()
        }
    }

    private var mRouteSource: RouteSource? = null
    private var mRouteForecastSource: RouteForecastSource? = null

    private var mListener: OnForecastListener? = null
    private var mLoading = false
    private val mDisposables = CompositeDisposable()

    private var mLoadingIndicator: View? = null
    private var mOriginField: EditText? = null
    private var mDestinationField: EditText? = null
    private var mRouteAdapter: ArrayAdapter<RouteForecast>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_pick_trip, container, false)
        mLoadingIndicator = view.findViewById(R.id.loading_indicator)
        mOriginField = view.findViewById(R.id.input_origin) as EditText
        mDestinationField = view.findViewById(R.id.input_destination) as EditText
        val mRouteList = view.findViewById(R.id.route_list) as ListView
        mRouteAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, emptyArray())
        mRouteList.adapter = mRouteAdapter

        mRouteList.setOnItemClickListener { adapterView, view, i, l ->
            onClickForecast(adapterView.getItemAtPosition(i) as RouteForecast)
        }

        view.findViewById(R.id.button_submit).setOnClickListener { v -> onClickOK() }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mListener = context as OnForecastListener?
            mRouteSource = context.routeSource
            mRouteForecastSource = context.routeForecastSource
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mDisposables.dispose()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other
     * fragments contained in that activity.
     *
     * See the Android Training lesson [Communicating
     * with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnForecastListener {
        fun onNextForecast(forecast: RouteForecast)
        fun onChooseForecast(forecast: RouteForecast)
    }

    fun onClickOK() {
        if (mLoading) return

        mLoading = true
        mLoadingIndicator?.visibility = View.VISIBLE

        val origin = mOriginField!!.text.toString()
        val destination = mDestinationField!!.text.toString()
        val date = System.currentTimeMillis() + 1000 * 60 * 60

        val disposeRoutes = mRouteSource!!.getRoutes(
                LocationQuery.ByName(origin),
                LocationQuery.ByName(destination),
                date)
                .toList()
                .flatMap { routes -> mRouteForecastSource!!.getRouteForecasts(routes, date).toList() }
                .doFinally { mLoading = false }
                .subscribe(
                        { routes -> onGotForecasts(routes) },
                        { error -> showEx(error) }
                )

        mDisposables.add(disposeRoutes)
    }

    fun onGotForecasts(forecasts: List<RouteForecast>) {
        mRouteAdapter?.clear()
        mRouteAdapter?.addAll(forecasts)
        mRouteAdapter?.notifyDataSetChanged()

        if (mListener == null) return
        for (forecast in forecasts) mListener!!.onNextForecast(forecast)
        if (forecasts.size == 1) mListener!!.onChooseForecast(forecasts[0])
    }

    fun onClickForecast(forecast: RouteForecast) {
        if (mListener != null) mListener!!.onChooseForecast(forecast)
    }

    fun showEx(ex: Throwable) {
        Log.w(TAG, ex)
    }
}

