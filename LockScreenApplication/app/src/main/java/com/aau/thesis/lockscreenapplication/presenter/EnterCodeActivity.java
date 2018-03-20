package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aau.thesis.lockscreenapplication.R;
import com.aau.thesis.lockscreenapplication.model.UnlockPhoneList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.makeFullScreen;
import static com.aau.thesis.lockscreenapplication.helper.Utilities.shutDownApp;

public class EnterCodeActivity extends Activity {
    EditText enter_code;
    TextView display_success;
    Boolean phoneLockStatus;
    String unlockCode;
    String totalScoreString;
    int totalScoreInt;
    int updatedTotalScoreInt;
    String phoneId;
    String phoneLockListID;
    String timePhoneWasUnlocked;

    String phoneLockReason;

    private FirebaseAuth firebaseAuth;
    DatabaseReference databasePhoneLockStatus;
    DatabaseReference databaseCode;
    DatabaseReference databaseTotal;
    DatabaseReference databasePhone;
    DatabaseReference databaseUnlockIdentifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        makeFullScreen(EnterCodeActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();
        String phoneId = firebaseAuth.getCurrentUser().getUid();

        databasePhoneLockStatus = FirebaseDatabase.getInstance().getReference("PhoneLockStatus");
        databaseCode = FirebaseDatabase.getInstance().getReference("Code");
        databaseTotal = FirebaseDatabase.getInstance().getReference("Total");
        databasePhone = FirebaseDatabase.getInstance().getReference("Phone");
        databaseUnlockIdentifier = FirebaseDatabase.getInstance().getReference("UnlockIdentifier");

        enter_code = (EditText) findViewById(R.id.enter_code);
        display_success = (TextView) findViewById(R.id.display_success);

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databasePhoneLockStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneLockStatus = dataSnapshot.getValue(Boolean.class);
                if (phoneLockStatus.equals(false)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            Toast toast= Toast.makeText(getApplicationContext(),"Code incorrect!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    public void addUnlockPhoneIdentifier(){
        phoneId = firebaseAuth.getCurrentUser().getUid();
        phoneLockListID = databaseUnlockIdentifier.push().getKey();
        phoneLockReason = "Entered Code";
        timePhoneWasUnlocked = "";
        UnlockPhoneList unlockPhoneIdentifier = new UnlockPhoneList(phoneLockListID, phoneLockReason, timePhoneWasUnlocked);
        databaseUnlockIdentifier.child(phoneId).child(phoneLockListID).setValue(unlockPhoneIdentifier);
        databaseUnlockIdentifier.child(phoneId).child(phoneLockListID).child("timestamp").setValue(ServerValue.TIMESTAMP);
    }

    public void addToTotalScore(){
        updatedTotalScoreInt = totalScoreInt - 1;
        String newTotalScoreString = Integer.toString(updatedTotalScoreInt);
        databaseTotal.setValue(newTotalScoreString);
    }

    public void changeUnlockPhoneStatus(){
        phoneId = firebaseAuth.getCurrentUser().getUid();
        Boolean phoneockStatus = false;
        databasePhone.child(phoneId).child("PhoneLockStatus").setValue(phoneockStatus);
    }
}
