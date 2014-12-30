package com.bluesierralabs.freewayforecast.Helpers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.bluesierralabs.freewayforecast.Models.Trip;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by timothy on 11/28/14.
 *
 * Example code from: http://developer.android.com/guide/topics/ui/controls/pickers.html
 */
public class DateSelectFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Trip tripInstance = Trip.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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
    }
}
