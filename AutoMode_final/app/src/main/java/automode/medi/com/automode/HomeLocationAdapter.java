package automode.medi.com.automode;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import automode.medi.com.automode.fragment.LocationFragment;
import automode.medi.com.automode.fragment.PlacesFragment;

/**
 * Created by Lenovo on 05-03-2017.
 */
public class HomeLocationAdapter extends FragmentStatePagerAdapter {

    public HomeLocationAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LocationFragment locationFragment = LocationFragment.getInstance();
                return locationFragment;
            case 1:
                PlacesFragment placesFragment = PlacesFragment.getInstance();
                return placesFragment;
            default:
                return null;
        }
    }





    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }


    @Override
    public int getCount() {
        return 2;
    }

}
