package eu.sheikhsoft.internetguard.Settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;


import eu.sheikhsoft.internetguard.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkFragment extends PreferenceFragment {


    public NetworkFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.networkpref);
    }

}
