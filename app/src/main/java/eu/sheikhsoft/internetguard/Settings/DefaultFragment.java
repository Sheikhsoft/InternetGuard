package eu.sheikhsoft.internetguard.Settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;




import eu.sheikhsoft.internetguard.R;
import eu.sheikhsoft.internetguard.ServiceSinkhole;


/**
 * A simple {@link Fragment} subclass.
 */
public class DefaultFragment extends PreferenceFragment{


    public DefaultFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.defaultpref);
    }



}
