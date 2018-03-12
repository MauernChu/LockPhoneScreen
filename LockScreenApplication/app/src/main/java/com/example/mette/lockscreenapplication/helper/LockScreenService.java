package com.example.mette.lockscreenapplication.helper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * The Service component can make is possible for running tasks in the background even when
 * the app is closed.
 * We are using this service for listening if the screen is on/of/booted.
 */

public class LockScreenService extends Service {
    BroadcastReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate() {
        //Start listening for the Screen On, Screen Off, and Boot completed actions
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);

        /*Set up a receiver to listen for the Intents in this Service, the reciever will
        * start the LoginActivity if the the screen was turned off or booted
        */
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, intentFilter);

        super.onCreate();
    }

    /**
     * The system invokes this method when the service is no longer used and is being destroyed.
     * The service needs to implement this to clean up any resources such as threads,
     * registered listeners, or receivers. This is the last call that the service receives.
     */
    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

    /*The Keyguard is basically the lockscreen implemented in android. It does not seem like
        * this code can actually be
        */
       /* KeyguardManager.KeyguardLock key;
        KeyguardManager keyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //This is deprecated, but it is a simple way to disable the lockscreen in code
        key = keyGuardManager.newKeyguardLock("IN");
        key.disableKeyguard();

        */