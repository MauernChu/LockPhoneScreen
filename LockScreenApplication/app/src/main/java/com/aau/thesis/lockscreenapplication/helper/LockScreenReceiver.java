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

    LockScreenReceiver() {
        DatabaseInterface databaseInterface = new FirebaseImpl();
        databaseInterface.listenToPhoneLockStatus();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_USER_PRESENT))
        {
            checkPhoneLockBoolean(context);
        }
    }


    public void checkPhoneLockBoolean(final Context context) {
        /** firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            databasePhone = FirebaseDatabase.getInstance().getReference("Phone");
            phoneID = firebaseAuth.getCurrentUser().getUid();
            databasePhone.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    phoneID = firebaseAuth.getCurrentUser().getUid();
                    boolean phoneLockStatus = dataSnapshot.child(phoneID).child("PhoneLockStatus").getValue(Boolean.class);

                    if (phoneLockStatus == true && !MainActivity.isActivityActive) {
                        Intent i = new Intent(context, MainActivity.class);
                        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                        i.setAction(Intent.ACTION_MAIN);
                        context.startActivity(i);
                        Log.e(LockScreenReceiver.class.getSimpleName(), "Starting Activity because PhoneLockStatus was: " + phoneLockStatus);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } **/

        boolean isPhoneLocked = PhoneLockStatusService.getInstance().isPhoneLocked();
        if(isPhoneLocked && !MainActivity.isActivityActive) {
            Intent i = new Intent(context, MainActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setAction(Intent.ACTION_MAIN);
            context.startActivity(i);
            Log.e(LockScreenReceiver.class.getSimpleName(), "Starting Activity because PhoneLockStatus was: " + isPhoneLocked);
        }
    }
}