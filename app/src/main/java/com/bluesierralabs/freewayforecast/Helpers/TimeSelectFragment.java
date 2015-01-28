package com.bluesierralabs.freewayforecast.Helpers;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.bluesierralabs.freewayforecast.Models.Trip;
import com.bluesierralabs.freewayforecast.R;

import java.util.Calendar;

/**
 * Created by timothy on 11/28/14.
 *
 * Starter code from: http://developer.android.com/guide/topics/ui/controls/pickers.html
 */
public class TimeSelectFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Trip tripInstance = Trip.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Set the trip with the hours and minutes.
        tripInstance.setTripStartWithTimeSelect(hourOfDay, minute);

        // Change edit text box in the activity
        EditText inputTime = (EditText) getActivity().findViewById(R.id.tripStartTime);
        inputTime.setText(tripInstance.getTripStartTimeReadable());
    }
}
