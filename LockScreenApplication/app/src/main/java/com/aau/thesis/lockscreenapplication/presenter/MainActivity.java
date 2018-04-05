package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aau.thesis.lockscreenapplication.R;
import com.aau.thesis.lockscreenapplication.data.DatabaseInterface;
import com.aau.thesis.lockscreenapplication.data.FirebaseImpl;
import com.aau.thesis.lockscreenapplication.data.PhoneLockStatusService;
import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;
import com.aau.thesis.lockscreenapplication.helper.LockScreenReceiver;
import com.aau.thesis.lockscreenapplication.helper.LockScreenService;
import com.aau.thesis.lockscreenapplication.model.UnlockPhoneList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.makeFullScreen;

public class MainActivity extends Activity implements PhoneLockStatusListener {
    public static boolean isActivityActive = true;


    String phoneId;
    String phoneLockListID;
    String phoneLockReason;
    String timePhoneWasUnlocked;
    String totalScoreString;
    int totalScoreInt;
    int newTotalScoreInt;
    String newTotalScoreString;
    boolean occured;


    DatabaseReference databasePhone;
    DatabaseReference databaseUnlockIdentifier;
    DatabaseReference databaseTotal;
    DatabaseReference databaseCodeEntered;


    //Used for creating phoneUser + checking if the users is already locked in
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        occured = false;
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        DatabaseInterface databaseInterface = new FirebaseImpl();
        databaseInterface.listenToPhoneLockStatus();
        databaseInterface.addListener(this);

        databasePhone = FirebaseDatabase.getInstance().getReference("Phone");
        databaseUnlockIdentifier = FirebaseDatabase.getInstance().getReference("UnlockIdentifier");
        databaseTotal = FirebaseDatabase.getInstance().getReference("Total");
        databaseCodeEntered = FirebaseDatabase.getInstance().getReference("CodeEntered");

        //Check if user has logged in or not, if not the Login-activity will open.
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(final FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    setContentView(R.layout.activity_main);
                    makeFullScreen(MainActivity.this);
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                }
            }
        };
    }

    //Method Creates a loop!
    @Override
    protected void onStart() {
        super.onStart();
        isActivityActive = true;
        firebaseAuth.addAuthStateListener(authStateListener);

        //Close the app if PhoneLockStatus returns false
        databasePhone.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String phoneId = firebaseAuth.getCurrentUser().getUid();
                Boolean phoneLockStatus = dataSnapshot.child(phoneId).child("PhoneLockStatus").getValue(Boolean.class);
                if (phoneLockStatus.equals(false) && occured == false) {
                    occured = true;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityActive = false;
    }

    //If the back-button is pressed, it will just return to the application.
    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }


    //Method for entering the activity for entering the code to unlock.
    public void goToEnterCodeActivity(View view) throws InterruptedException {
        Intent intent = new Intent(this, EnterCodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //method for logging, when they push Home button or Recent apps button
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        phoneId = firebaseAuth.getCurrentUser().getUid();
        phoneLockListID = databaseUnlockIdentifier.push().getKey();
        phoneLockReason = "Home key";
        timePhoneWasUnlocked = "";
        UnlockPhoneList unlockPhoneIdentifier = new UnlockPhoneList(phoneLockListID, phoneLockReason, timePhoneWasUnlocked);
        databaseUnlockIdentifier.child(phoneId).child(phoneLockListID).setValue(unlockPhoneIdentifier);
        databaseUnlockIdentifier.child(phoneId).child(phoneLockListID).child("timestamp").setValue(ServerValue.TIMESTAMP);
        databaseTotal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalScoreString = dataSnapshot.getValue(String.class);
                totalScoreInt = Integer.parseInt(totalScoreString);
                newTotalScoreInt = totalScoreInt - 1;
                newTotalScoreString = Integer.toString(newTotalScoreInt);
                databaseTotal.setValue(newTotalScoreString);
                databaseCodeEntered.setValue("0");
                databasePhone.child(phoneId).child("PhoneLockStatus").setValue(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finish();
    }

    @Override
    public void PhoneLockStatusChanged(boolean isPhoneLocked) {
        if(isPhoneLocked && !MainActivity.isActivityActive) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setAction(Intent.ACTION_MAIN);
            startActivity(i);
            Log.e(MainActivity.class.getSimpleName(), "Starting Activity because PhoneLockStatus was: " + isPhoneLocked);
        }
    }
}