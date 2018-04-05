package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.aau.thesis.lockscreenapplication.data.DatabaseInterface;
import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


public abstract class BaseActivity extends Activity implements PhoneLockStatusListener {



    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }
}

