package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aau.thesis.lockscreenapplication.R;
import com.aau.thesis.lockscreenapplication.data.DatabaseInterface;
import com.aau.thesis.lockscreenapplication.data.FirebaseImpl;
import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;
import com.aau.thesis.lockscreenapplication.model.UnlockPhoneList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.makeFullScreen;

public class MainActivity extends Activity implements PhoneLockStatusListener {
    public static boolean isActivityActive = true;

    private String phoneId;
    private String phoneLockListID;
    private String phoneLockReason;
    private String timePhoneWasUnlocked;
    private String totalScoreString;
    private int totalScoreInt;
    private int newTotalScoreInt;
    private String newTotalScoreString;
    private boolean occured;

    private DatabaseInterface databaseInterface;
    private DatabaseReference databaseUnlockIdentifier;
    private DatabaseReference databaseTotal;
    private DatabaseReference databaseCodeEntered;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        occured = false;
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        databaseInterface = FirebaseImpl.getInstance();
        databaseInterface.addListener(this);

        firebaseAuth = databaseInterface.createFirebaseAuth();
        databaseTotal = databaseInterface.createDatabaseReferenceToTotal();
        databaseUnlockIdentifier = databaseInterface.createDatabaseReferenceToUnlockIdentifier();
        databaseCodeEntered = databaseInterface.createDatabaseReferenceToCodeEntered();
    }

    @Override
    protected void onStart() {
        super.onStart();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(final FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    String id = currentUser.getUid();
                    setContentView(R.layout.activity_main);
                    makeFullScreen(MainActivity.this);

                }
            }
        };

        isActivityActive = true;
        firebaseAuth.addAuthStateListener(authStateListener);
        databaseInterface.listenToPhoneLockStatus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityActive = false;
    }

    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }


    public void goToEnterCodeActivity(View view) throws InterruptedException {
        Intent intent = new Intent(this, EnterCodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (firebaseAuth.getCurrentUser() != null) {
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
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            finish();
        }
    }

    @Override
    public void PhoneLockStatusChanged(boolean phoneLockStatusToString) {
        if (firebaseAuth.getCurrentUser() != null) {
            if (!phoneLockStatusToString) {
                Intent intent = new Intent(getApplicationContext(), this.getClass());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            } else if (phoneLockStatusToString && !MainActivity.isActivityActive) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setAction(Intent.ACTION_MAIN);
                startActivity(i);
            }
        }
    }
}