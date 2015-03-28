package com.bluesierralabs.freewayforecast.helpers;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.bluesierralabs.freewayforecast.R;

/**
 * Created by timothy on 1/5/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
    }
}
