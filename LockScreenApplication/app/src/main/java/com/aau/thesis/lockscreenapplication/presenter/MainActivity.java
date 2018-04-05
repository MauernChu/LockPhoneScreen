package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aau.thesis.lockscreenapplication.R;
import com.aau.thesis.lockscreenapplication.data.DatabaseInterface;
import com.aau.thesis.lockscreenapplication.data.FirebaseImpl;
import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;
import com.aau.thesis.lockscreenapplication.helper.LockScreenService;
import com.aau.thesis.lockscreenapplication.model.UnlockPhoneList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.makeFullScreen;

public class MainActivity extends BaseActivity implements PhoneLockStatusListener {
    public static boolean isActivityActive = true;
    private DatabaseInterface databaseInterface;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databasePhone;
    private DatabaseReference databaseUnlockIdentifier;
    private DatabaseReference databaseTotal;
    private DatabaseReference databaseCodeEntered;

    private String phoneId;
    private String phoneLockListID;
    private String phoneLockReason;
    private String timePhoneWasUnlocked;
    private String totalScoreString;
    private int totalScoreInt;
    private int newTotalScoreInt;
    private String newTotalScoreString;
    private boolean occured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        occured = false;
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        //--------- Database Logic ---------
        databaseInterface = new FirebaseImpl();
        databaseInterface.addListener(this);
        firebaseAuth = databaseInterface.createFirebaseAuth();
        databasePhone = databaseInterface.createDatabaseReferenceToPhone();
        databaseUnlockIdentifier = databaseInterface.createDatabaseReferenceToUnlockIdentifier();
        databaseTotal = databaseInterface.createDatabaseReferenceToTotal();
        databaseCodeEntered = databaseInterface.createDatabaseReferenceToCodeEntered();
        firebaseAuth = databaseInterface.createFirebaseAuth();

        checkIfUserIsCheckedIn();
    }

    private void checkIfUserIsCheckedIn() {
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

    @Override
    protected void onStart() {
        super.onStart();
        isActivityActive = true;
        firebaseAuth.addAuthStateListener(authStateListener);
        databaseInterface.listenToPhoneLockStatus();
    }

    @Override
    public void PhoneLockStatusChanged(boolean isPhoneLocked) {
        if (isPhoneLocked && !MainActivity.isActivityActive) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setAction(Intent.ACTION_MAIN);
            startActivity(i);
            Log.e(MainActivity.class.getSimpleName(), "Starting Activity because PhoneLockStatus was: " + isPhoneLocked);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityActive = false;
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
}