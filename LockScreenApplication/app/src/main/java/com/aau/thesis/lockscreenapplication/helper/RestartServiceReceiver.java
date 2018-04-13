package com.aau.thesis.lockscreenapplication.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aau.thesis.lockscreenapplication.data.DatabaseInterface;
import com.aau.thesis.lockscreenapplication.data.FirebaseImpl;
import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;
import com.aau.thesis.lockscreenapplication.presenter.EnterCodeActivity;
import com.aau.thesis.lockscreenapplication.presenter.LoginActivity;
import com.aau.thesis.lockscreenapplication.presenter.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RestartServiceReceiver extends BroadcastReceiver {
    private static final String TAG = "RestartServiceReceiver";
    private DatabaseInterface databaseInterface;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseTestPhone;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        context.startService(new Intent(context.getApplicationContext(), StickyService.class));
        firebaseAuth = FirebaseAuth.getInstance();

        if (action.equals(Intent.ACTION_USER_PRESENT) || action.equals(Intent.ACTION_SCREEN_ON) ||  action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_TIME_TICK)
                || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            test(context);

        }
    }

    public void test(final Context context) {
       if (firebaseAuth.getCurrentUser() != null) {
           databaseTestPhone = FirebaseDatabase.getInstance().getReference("Phone");
            String idCurrentUser = firebaseAuth.getCurrentUser().getUid();
            Query namecheck = databaseTestPhone.orderByChild("Name");
            databaseTestPhone.child(idCurrentUser).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    String name = dataSnapshot.getValue(String.class);
                    if (name.equals("Himette")) {
                        Intent i = new Intent(context, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                        i.setAction(Intent.ACTION_MAIN);
                        context.startActivity(i);
                    } else {
                        Intent i = new Intent(context, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("EXIT", true);
                        context.startActivity(i);
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
           Intent loginIntent = new Intent(context, LoginActivity.class);
           context.startActivity(loginIntent);
       }

    }

}