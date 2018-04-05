package com.aau.thesis.lockscreenapplication.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aau.thesis.lockscreenapplication.data.DatabaseInterface;
import com.aau.thesis.lockscreenapplication.data.FirebaseImpl;
import com.aau.thesis.lockscreenapplication.data.PhoneLockStatusService;
import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;
import com.aau.thesis.lockscreenapplication.presenter.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LockScreenReceiver extends BroadcastReceiver {

    private FirebaseAuth firebaseAuth;
    DatabaseReference databasePhone;
    String phoneID;


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_USER_PRESENT))
        {
            checkPhoneLockBoolean(context);
        }
    }


    public void checkPhoneLockBoolean(final Context context) {


        boolean isPhoneLocked = PhoneLockStatusService.getInstance().isPhoneLocked();
        if(isPhoneLocked && !MainActivity.isActivityActive) {
            Intent i = new Intent(context, MainActivity.class);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setAction(Intent.ACTION_MAIN);
            context.startActivity(i);
        }
    }
}