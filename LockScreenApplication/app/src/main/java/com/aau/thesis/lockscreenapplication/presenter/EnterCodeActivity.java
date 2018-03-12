package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aau.thesis.lockscreenapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.shutDownApp;

public class EnterCodeActivity extends Activity {
    EditText enter_code;
    TextView display_success;
    Boolean phoneLockStatus;
    String unlockCode;

    DatabaseReference firebasePhoneLockStatus;
    DatabaseReference firebaseCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        makeFullScreen();

        firebasePhoneLockStatus = FirebaseDatabase.getInstance().getReference("PhoneLockStatus");
        firebaseCode = FirebaseDatabase.getInstance().getReference("Code");

        enter_code = (EditText) findViewById(R.id.enter_code);
        display_success = (TextView) findViewById(R.id.display_success);

    }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebasePhoneLockStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneLockStatus = dataSnapshot.getValue(Boolean.class);
                if (phoneLockStatus.equals(false)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    shutDownApp();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseCode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unlockCode = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void unlockScreen(View view) throws InterruptedException {
        checkEnteredCode(unlockCode);
    }

    public void checkEnteredCode(String databaseCode){
        String unlockCode = enter_code.getText().toString().trim();
        if(unlockCode.equals(databaseCode)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            //shutDownApp();
            //System.exit(1);
        }else{
            display_success.setText("wrong code!");
        }
    }


}
