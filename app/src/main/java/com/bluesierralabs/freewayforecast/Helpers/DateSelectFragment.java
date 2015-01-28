package com.bluesierralabs.freewayforecast.Helpers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by timothy on 11/28/14.
 *
 * Example code from: http://developer.android.com/guide/topics/ui/controls/pickers.html
 */
public class DateSelectFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

//    public static final String ARG_YEAR = "year";
//    public static final String ARG_MONTH = "month";
//    public static final String ARG_DAY = "day";

    private Trip tripInstance = Trip.getInstance();

//    private DatePickerDialog.OnDateSetListener listener;
//
//    public static DateSelectFragment newInstance(DatePickerDialog.OnDateSetListener listener, int year, int month, int day) {
//        final DateSelectFragment date_picker = new DateSelectFragment();
//        date_picker.setListener(listener);
//
//        final Bundle arguments = new Bundle();
//        arguments.putInt(ARG_YEAR, year);
//        arguments.putInt(ARG_MONTH, month);
//        arguments.putInt(ARG_DAY, day);
//        date_picker.setArguments(arguments);
//
//        return date_picker;
//    }
//
//    private void setListener(DatePickerDialog.OnDateSetListener listener) {
//        this.listener = listener;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        tripInstance.setTripStartWithDateSelect(year, month, day);

        // Change edit text box in the activity
        EditText inputDate = (EditText) getActivity().findViewById(R.id.tripStartDate);
        inputDate.setText(tripInstance.getTripStartDateReadable());
    }

    @Override
    public void onDestroyView() {

        Log.e("DialogFragment closing", "onDestroyView");

        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();

        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_splash_screen,null);
        view.findViewById(R.id.tripStartDate).clearFocus();
    }
}
