package automode.medi.com.automode.recievers;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ist on 3/5/17.
 */

public class ScreenService extends Service {
    private ScreenStateReceiver screenStateReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        registerReciever();
    }

    private void registerReciever() {
    /**************** Register Screen State Receiver START ************/
        screenStateReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        this.registerReceiver(screenStateReceiver, filter);
    }
}
