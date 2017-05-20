package automode.medi.com.automode.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import automode.medi.com.automode.CurrentModeActivity;
import automode.medi.com.automode.InputModeActivity;
import automode.medi.com.automode.R;
import automode.medi.com.automode.SavedLocationHistoryActivity;

/**
 * Created by Lenovo on 05-03-2017.
 */
public class LocationFragment extends Fragment implements  View.OnClickListener {
    View rootView;
    ImageView currentMode_view,inputModeView,savedModeView;
    private static LocationFragment locationFragment;
    Context mContext;

    public static LocationFragment getInstance() {
//        if (locationFragment == null) {
            locationFragment = new LocationFragment();
//        }
        return locationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (LinearLayout)inflater.inflate(R.layout.layout_location_fragment,container,false);
        mContext=getActivity();
        currentMode_view=(ImageView) rootView.findViewById(R.id.tv_current_mode);
        inputModeView=(ImageView)rootView.findViewById(R.id.tv_input_location);
        savedModeView=(ImageView)rootView.findViewById(R.id.tv_saved_location);
        inputModeView.setOnClickListener(this);
        currentMode_view.setOnClickListener(this);
        savedModeView.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_current_mode: {
                Intent intentCurrentMode = new Intent(mContext, CurrentModeActivity.class);
                startActivity(intentCurrentMode);
                break;
            }
            case R.id.tv_input_location: {
                Intent intentCurrentMode = new Intent(mContext, InputModeActivity.class);
                startActivity(intentCurrentMode);
                break;
            }
            case R.id.tv_saved_location: {
                Intent savedModeintent = new Intent(mContext, SavedLocationHistoryActivity.class);
                startActivity(savedModeintent);
                break;
            }

        }
        }
}
