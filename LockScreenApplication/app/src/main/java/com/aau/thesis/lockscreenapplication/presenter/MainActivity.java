package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aau.thesis.lockscreenapplication.R;
import com.aau.thesis.lockscreenapplication.helper.HomeWatcher;
import com.aau.thesis.lockscreenapplication.helper.LockScreenService;
import com.aau.thesis.lockscreenapplication.helper.OnHomePressedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.makeFullScreen;

public class MainActivity extends Activity {
    //Temporary Database reference for storing homekey pushed on the screen + list to store values
    DatabaseReference databaseHomeKey;
    List<Boolean> homeLockList;

    //Instans of databasereference + boolean for storing the PhoneLockStatus
    DatabaseReference firebasePhoneLockStatus;
    Boolean phoneLockStatus;

    //Used for creating phoneUser + checking if the users is already locked in
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This method is called whenever the code for closing the app is being called in order to
        //all activities in the app.
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        //Instans of the databasereference for getting Boolean value from the PhoneLockStatus
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

                    // Set up the Lockscreen by setting the screen to full
                    makeFullScreen(MainActivity.this);

                    //Making an instance of the Service for listening to phone startup after booted
                    startService(new Intent(getApplicationContext(), LockScreenService.class));

                    //Logging if the Homekey is pressed. Stores this in the database
                    databaseHomeKey = FirebaseDatabase.getInstance().getReference("UnlockPhoneList");
                    homeLockList = new ArrayList<>();
                    HomeWatcher MainHomeWatcher = new HomeWatcher(getApplicationContext());
                    MainHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
                        @Override
                        public void onHomePressed() {
                            homeLockList.clear();
                            String idHomeKey = databaseHomeKey.push().getKey();
                            Boolean homeKeyPushed = true;
                            homeLockList.add(homeKeyPushed);
                           // UnlockPhoneList homeKey = new UnlockPhoneList(homeLockList, idHomeKey);
                           // databaseHomeKey.child(idHomeKey).setValue(homeKey);
                        }

                        @Override
                        public void onHomeLongPressed() {
                        }
                    });
                    MainHomeWatcher.startWatch();
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);

        //Close the app if PhoneLockStatus returns false
        firebasePhoneLockStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneLockStatus = dataSnapshot.getValue(Boolean.class);
                if (phoneLockStatus.equals(false)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }
}