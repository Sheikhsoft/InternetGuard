package eu.sheikhsoft.internetguard.Settings;
/**
 * Created by Sk Shamimul islam on 08/09/2017.
 */


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;



public class SectionsPagerAdepter extends FragmentPagerAdapter {

    public SectionsPagerAdepter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // this is just to show it compiles
        switch (position){
            case 0:
                DefaultFragment defaultFragment = new DefaultFragment();
                return defaultFragment;
            case 1:
                GeneralFragment generalFragment = new GeneralFragment();
                return generalFragment;
            case 2:
                NetworkFragment networkFragment = new NetworkFragment();
                return  networkFragment;
            case 3:
                AdvancedFragment advancedFragment = new AdvancedFragment();
                return  advancedFragment;
            case 4:
                SpeedFrag speedFrag = new SpeedFrag();
                return  speedFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Default";
            case 1:
                return "General";
            case 2:
                return "Network";
            case 3:
                return "Advanced";
            case 4:
                return "Speed Notification";
            default:
                return null;
        }
    }
}

