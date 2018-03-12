package com.aau.thesis.lockscreenapplication.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by mette on 12/03/2018.
 */

public abstract class Utilities {


    //Method for shutting down application.
    public static void shutDownApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


    //This method sets the screen to fullscreen. It removes the Notifications bar,
    //the Actionbar and the virtual keys (if they are on the phone)
    public static void makeFullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            activity.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            activity.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

}
