package com.aau.thesis.lockscreenapplication.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
        //If the screen was just turned on or it just booted up, start your Lock Activity
        if (action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_TIME_TICK)) {
            checkPhoneLockBoolean(context);
        }
    }


    public void checkPhoneLockBoolean(final Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            databasePhone = FirebaseDatabase.getInstance().getReference("Phone");
            phoneID = firebaseAuth.getCurrentUser().getUid();
            databasePhone.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    phoneID = firebaseAuth.getCurrentUser().getUid();
                    Boolean phoneLockStatus = dataSnapshot.child(phoneID).child("PhoneLockStatus").getValue(Boolean.class);
                    if (phoneLockStatus.equals(true)) {
                        Intent i = new Intent(context, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}