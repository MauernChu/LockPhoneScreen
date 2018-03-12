package com.example.mette.lockscreenapplication.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.mette.lockscreenapplication.R;
import com.example.mette.lockscreenapplication.helper.LockScreenService;
import com.example.mette.lockscreenapplication.model.Phone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends Activity {
    EditText editPhoneName;
    Button button_login;

    DatabaseReference databasePhone;
    private FirebaseAuth firebaseAuthLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        makeFullScreen();
        startService(new Intent(this, LockScreenService.class));

        firebaseAuthLogin = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        databasePhone = FirebaseDatabase.getInstance().getReference("Phone");

        editPhoneName = (EditText) findViewById(R.id.phonename);
        button_login = (Button) findViewById(R.id.button_login);

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
        return; //Do nothing!
    }

    public void goMainActivity(View view) throws InterruptedException {
        addPhoneToDatabase();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addPhoneToDatabase() {
        final String phoneName = editPhoneName.getText().toString().trim();
        String defaultPassword = "TestName";
        String defaultEmail = "test@test.dk";
        progressDialog.setMessage("Signing up...");
        progressDialog.show();
        firebaseAuthLogin.createUserWithEmailAndPassword(defaultEmail, defaultPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String phoneId = firebaseAuthLogin.getCurrentUser().getUid();
                    Phone dbPhone = new Phone(phoneId, phoneName);
                    DatabaseReference databaseCurrentUser = databasePhone.child(phoneId);
                    databaseCurrentUser.child("Name").setValue(dbPhone.getPhoneName());
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databasePhone.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
