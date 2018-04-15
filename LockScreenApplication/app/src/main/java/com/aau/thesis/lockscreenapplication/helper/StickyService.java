package com.aau.thesis.lockscreenapplication.helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class StickyService extends Service {
    private static final String TAG = "StickyService";


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }
}
