package com.aau.thesis.lockscreenapplication.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aau.thesis.lockscreenapplication.R;
import com.aau.thesis.lockscreenapplication.data.DatabaseInterface;
import com.aau.thesis.lockscreenapplication.data.FirebaseImpl;
import com.aau.thesis.lockscreenapplication.model.UnlockPhoneList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.makeFullScreen;

public class EnterCodeActivity extends BaseActivity {
    private DatabaseInterface databaseInterface;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databasePhoneLockStatus;
    private DatabaseReference databaseCode;
    private DatabaseReference databaseTotal;
    private DatabaseReference databasePhone;
    private DatabaseReference databaseUnlockIdentifier;
    private DatabaseReference databaseCodeEntered;

    private EditText enter_code;
    private TextView display_success;

    private String unlockCode;
    private String totalScoreString;
    private String newTotalScoreString;
    private String phoneLockReason;
    private String phoneId;
    private String phoneLockListID;
    private String timePhoneWasUnlocked;
    private int totalScoreInt;
    private int updatedTotalScoreInt;
    private int newTotalScoreInt;
    private boolean occured;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        occured = false;
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        setContentView(R.layout.activity_enter_code);

        //--------- View related ---------
        makeFullScreen(EnterCodeActivity.this);
        enter_code = (EditText) findViewById(R.id.enter_code);
        display_success = (TextView) findViewById(R.id.display_success);

       databasePhone = FirebaseDatabase.getInstance().getReference("Phone");


        //--------- Database Logic ---------
        databaseInterface = FirebaseImpl.getInstance();
        databaseInterface.addListener(this);
        firebaseAuth = databaseInterface.createFirebaseAuth();
        databasePhoneLockStatus = databaseInterface.createDatabaseReferenceToPhoneLockStatus();
        databaseCode = databaseInterface.createDatabaseReferenceToCode();
        databaseTotal = databaseInterface.createDatabaseReferenceToTotal();
        databaseUnlockIdentifier = databaseInterface.createDatabaseReferenceToUnlockIdentifier();
        databaseCodeEntered = databaseInterface.createDatabaseReferenceToCodeEntered();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseInterface.listenToPhoneLockStatus();
        fetchUnlockCodeChange();
        fetchTotalScore();
    }

    @Override
    public void PhoneLockStatusChanged(boolean phoneLockStatusToString) {
        if (firebaseAuth.getCurrentUser() != null) {
            if (!phoneLockStatusToString) {
                Intent intent = new Intent(getApplicationContext(), this.getClass());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        }
    }


    private void fetchUnlockCodeChange() {
        databaseCode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unlockCode = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void fetchTotalScore() {
        databaseTotal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalScoreString = dataSnapshot.getValue(String.class);
                totalScoreInt = Integer.parseInt(totalScoreString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void unlockScreen(View view) throws InterruptedException {
        checkEnteredCode(unlockCode);
    }

    public void checkEnteredCode(String databaseCode) {
        String unlockCode = enter_code.getText().toString().trim();
        if (unlockCode.equals(databaseCode)) {
            addToTotalScore();
            addUnlockPhoneIdentifier();
            changeName();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Code incorrect!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    public void changeName() {
        phoneId = firebaseAuth.getCurrentUser().getUid();
        String changeName = "Hmette";
        databasePhone.child(phoneId).child("Name").setValue(changeName);
    }

    public void addUnlockPhoneIdentifier() {
        phoneId = firebaseAuth.getCurrentUser().getUid();
        phoneLockListID = databaseUnlockIdentifier.push().getKey();
        phoneLockReason = "Entered Code";
        timePhoneWasUnlocked = "";
        UnlockPhoneList unlockPhoneIdentifier = new UnlockPhoneList(phoneLockListID, phoneLockReason, timePhoneWasUnlocked);
        databaseUnlockIdentifier.child(phoneId).child(phoneLockListID).setValue(unlockPhoneIdentifier);
        databaseUnlockIdentifier.child(phoneId).child(phoneLockListID).child("timestamp").setValue(ServerValue.TIMESTAMP);
    }

    public void addToTotalScore() {
        updatedTotalScoreInt = totalScoreInt - 1;
        String newTotalScoreString = Integer.toString(updatedTotalScoreInt);
        databaseTotal.setValue(newTotalScoreString);
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
            newTotalScoreInt = totalScoreInt - 1;
            newTotalScoreString = Integer.toString(newTotalScoreInt);
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

  /*  public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }*/
}
