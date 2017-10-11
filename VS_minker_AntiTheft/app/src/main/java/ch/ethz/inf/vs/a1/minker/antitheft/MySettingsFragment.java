package ch.ethz.inf.vs.a1.minker.antitheft;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MySettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean prefEnabled = getArguments().getBoolean("prefEnabled");
        getPreferenceManager().setSharedPreferencesName(getString(R.string.sharedprefs));

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        getPreferenceScreen().setEnabled(prefEnabled);

        //TODO: enable choosing of sensor only if that sensor exists
    }
}
