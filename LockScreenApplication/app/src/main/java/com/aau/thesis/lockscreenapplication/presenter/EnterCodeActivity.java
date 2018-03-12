package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aau.thesis.lockscreenapplication.R;
import com.aau.thesis.lockscreenapplication.model.UnlockPhoneIdentifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.makeFullScreen;

public class EnterCodeActivity extends Activity {
    EditText enter_code;
    TextView display_success;
    Boolean phoneLockStatus;
    String unlockCode;
    String totalScoreString;
    int totalScoreInt;
    int newTotalScoreInt;
    String phoneId;
    String enteredCodeIdentifierID;
    String timestamp;

    String enteredCodeIdentifier;

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
        databaseUnlockIdentifier = FirebaseDatabase.getInstance().getReference("UnlockIdentifier").child(phoneId);

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
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            display_success.setText("wrong code!");
        }
    }

    public void addUnlockPhoneIdentifier(){
        phoneId = firebaseAuth.getCurrentUser().getUid();
        enteredCodeIdentifierID = databaseUnlockIdentifier.push().getKey();
        enteredCodeIdentifier = "Entered Code";
        timestamp = "1520853486553";
        UnlockPhoneIdentifier unlockPhoneIdentifier = new UnlockPhoneIdentifier(enteredCodeIdentifierID, enteredCodeIdentifier, timestamp);
        databaseUnlockIdentifier.setValue(unlockPhoneIdentifier);
    }

    public void addToTotalScore(){
        newTotalScoreInt = totalScoreInt + 1;
        String newTotalScoreString = Integer.toString(newTotalScoreInt);
        databaseTotal.setValue(newTotalScoreString);
    }
}
