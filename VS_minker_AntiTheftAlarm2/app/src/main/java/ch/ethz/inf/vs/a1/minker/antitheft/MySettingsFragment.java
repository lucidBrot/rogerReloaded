package ch.ethz.inf.vs.a1.minker.antitheft;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MySettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        //TODO: remove the other content of SettingActivity
        //TODO: enable choosing of sensor
        //TODO: enable choosing of sensor only if that sensor exists
    }
}
