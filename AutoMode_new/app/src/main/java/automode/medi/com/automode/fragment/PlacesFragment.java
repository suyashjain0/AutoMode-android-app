package automode.medi.com.automode.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import automode.medi.com.automode.CollegeActivity;
import automode.medi.com.automode.R;


/**
 * Created by Lenovo on 05-03-2017.
 */
public class PlacesFragment extends Fragment implements View.OnClickListener {
    View rootView;
    Context mContext;
    ImageView college_view;
    private static PlacesFragment placesFragment;

    public static PlacesFragment getInstance() {
//        if (placesFragment == null) {
        placesFragment = new PlacesFragment();
//        }
        return placesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (LinearLayout) inflater.inflate(R.layout.layout_places_fragment, container, false);
        mContext = getActivity();
        college_view = (ImageView) rootView.findViewById(R.id.tv_college_tab);
        college_view.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_college_tab: {
                Intent intentCollege = new Intent(mContext, CollegeActivity.class);
                startActivity(intentCollege);
                break;
            }
        }

    }
}