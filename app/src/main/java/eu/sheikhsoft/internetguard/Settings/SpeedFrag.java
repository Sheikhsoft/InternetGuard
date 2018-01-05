package eu.sheikhsoft.internetguard.Settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import eu.sheikhsoft.internetguard.R;

/**
 * Created by Sk Shamimul islam on 12/18/2017.
 */

public class SpeedFrag extends PreferenceFragment {
    public SpeedFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.speedpref);
    }
}
