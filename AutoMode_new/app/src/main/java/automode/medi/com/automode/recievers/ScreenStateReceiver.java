package automode.medi.com.automode.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import automode.medi.com.automode.holder.SavedLocations;
import automode.medi.com.automode.utils.AppUtils;


public class ScreenStateReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenStateReceiver.class.getSimpleName();


    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "RQ:onReceive()");
        List<SavedLocations> savedLocationsList = AppUtils.getInstance(context).getDataofSavedLocation();
        if(savedLocationsList!=null) {
            for (SavedLocations savedLocations : savedLocationsList) {
                if (savedLocations != null && savedLocations.getLattitude() != null && savedLocations.getLongitude() != null) {
                    Boolean changeMode = AppUtils.getInstance(context).getEventToChangeTheModeOfPhone(savedLocations.getLattitude(), savedLocations.getLongitude());
                    if (changeMode && savedLocations.getMode() != null) {
                        AppUtils.getInstance(context).setModeAccordingToSavedLocation(savedLocations.getMode());
                        break;
                    }
                }
            }

        }

    }
}
