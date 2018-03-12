package com.example.mette.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.mette.lockscreenapplication.R;
import com.example.mette.lockscreenapplication.helper.HomeWatcher;
import com.example.mette.lockscreenapplication.helper.LockScreenService;
import com.example.mette.lockscreenapplication.helper.OnHomePressedListener;
import com.example.mette.lockscreenapplication.model.HomeKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.mette.lockscreenapplication.helper.Utilities.shutDownApp;

public class MainActivity extends Activity {
    //Temporary Database reference for storing homekey pushed on the screen
    DatabaseReference databaseHomeKey;
    List<Boolean> homeLockList;
    Boolean phoneLockStatus;

    DatabaseReference firebasePhoneLockStatus;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebasePhoneLockStatus = FirebaseDatabase.getInstance().getReference("PhoneLockStatus");
        //Check if user has locked in or not, if not the Login-activity will open.

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    setContentView(R.layout.activity_main);
                }
            }
        };


        // Set up the Lockscreen by setting the screen to full
        makeFullScreen();
        //Making an instance of the Service for listening to phone startup after booted
        startService(new Intent(this, LockScreenService.class));

        //Logging the if the Homekey is pressed. Stores this in the database
        databaseHomeKey = FirebaseDatabase.getInstance().getReference("HomeKey");
        homeLockList = new ArrayList<>();
        HomeWatcher MainHomeWatcher = new HomeWatcher(this);
        MainHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                homeLockList.clear();
                String idHomeKey = databaseHomeKey.push().getKey();
                Boolean homeKeyPushed = true;
                homeLockList.add(homeKeyPushed);
                HomeKey homeKey = new HomeKey(homeLockList, idHomeKey);
                databaseHomeKey.child(idHomeKey).setValue(homeKey);
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        MainHomeWatcher.startWatch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        firebasePhoneLockStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneLockStatus = dataSnapshot.getValue(Boolean.class);
                if (phoneLockStatus.equals(false)) {
                    shutDownApp();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //This method sets the screen to fullscreen. It removes the Notifications bar,
    //the Actionbar and the virtual keys (if they are on the phone)
    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }


    //If the back-button is pressed, it will just return to the application. This is for avoiding the
    //user to use the back-button to go out of the app
    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }


    //Method for entering the activity for entering the code to unlock.
    public void goToEnterCodeActivity(View view) throws InterruptedException {
        Intent intent = new Intent(this, EnterCodeActivity.class);
        startActivity(intent);
    }
}