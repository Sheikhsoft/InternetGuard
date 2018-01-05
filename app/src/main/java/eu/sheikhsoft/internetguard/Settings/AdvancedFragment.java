package eu.sheikhsoft.internetguard.Settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import eu.sheikhsoft.internetguard.R;

public class AdvancedFragment extends PreferenceFragment {


    public AdvancedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.advancepref);
    }

}
