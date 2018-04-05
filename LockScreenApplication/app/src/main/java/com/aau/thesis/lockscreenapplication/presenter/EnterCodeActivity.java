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


        //--------- Database Logic ---------
        databaseInterface = new FirebaseImpl();
        databaseInterface.addListener(this);
        firebaseAuth = databaseInterface.createFirebaseAuth();
        databasePhoneLockStatus = databaseInterface.createDatabaseReferenceToPhoneLockStatus();
        databaseCode = databaseInterface.createDatabaseReferenceToCode();
        databaseTotal = databaseInterface.createDatabaseReferenceToTotal();
        databasePhone = databaseInterface.createDatabaseReferenceToPhone();
        databaseUnlockIdentifier = databaseInterface.createDatabaseReferenceToUnlockIdentifier();
        databaseCodeEntered = databaseInterface.createDatabaseReferenceToCodeEntered();
    }



    @Override
    protected void onStart() {
        super.onStart();
        databaseInterface.listenToPhoneLockStatus();


        databaseCode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unlockCode = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            changeUnlockPhoneStatus();
            changeCodeEntered();
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

    public void changeUnlockPhoneStatus() {
        phoneId = firebaseAuth.getCurrentUser().getUid();
        Boolean phoneockStatus = false;
        databasePhone.child(phoneId).child("PhoneLockStatus").setValue(phoneockStatus);
    }

    public void changeCodeEntered() {
        databaseCodeEntered.setValue("0");
    }

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
        if (isPhoneLocked == false) {
            Intent intent = new Intent(getApplicationContext(), this.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }
}
