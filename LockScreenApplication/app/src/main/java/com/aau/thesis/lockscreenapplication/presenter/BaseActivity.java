package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;

public abstract class BaseActivity extends Activity implements PhoneLockStatusListener {

    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }
}

