package com.aau.thesis.lockscreenapplication;

import android.app.Application;
import android.content.Context;

public class CustomApplication extends Application {
    public static CustomApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static CustomApplication getInstance() {
        return instance;
    }
}
