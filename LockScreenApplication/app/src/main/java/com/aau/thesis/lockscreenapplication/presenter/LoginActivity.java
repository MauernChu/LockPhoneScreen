package com.aau.thesis.lockscreenapplication.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aau.thesis.lockscreenapplication.R;
import com.aau.thesis.lockscreenapplication.helper.LockScreenService;
import com.aau.thesis.lockscreenapplication.model.Phone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.aau.thesis.lockscreenapplication.helper.Utilities.makeFullScreen;

public class LoginActivity extends Activity {
    //EditText and button for storing phoneUser in database and login
    EditText editPhoneName;
    EditText editEmail;
    EditText editPassword;
    Button button_login;

    DatabaseReference databasePhone;
    DatabaseReference firebasePhoneLockStatus;
    private FirebaseAuth firebaseAuthLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        makeFullScreen(LoginActivity.this);
        startService(new Intent(this, LockScreenService.class));

        firebasePhoneLockStatus = FirebaseDatabase.getInstance().getReference("PhoneLockStatus");

        firebaseAuthLogin = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        databasePhone = FirebaseDatabase.getInstance().getReference("Phone");

        editPhoneName = (EditText) findViewById(R.id.phonename);
        editEmail = (EditText) findViewById(R.id.Email);
        editPassword = (EditText) findViewById(R.id.Password);
        button_login = (Button) findViewById(R.id.button_login);
    }


    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }

    //Method for entering MainActivity when Login button is pushed
    public void goMainActivity(View view) throws InterruptedException {
        addPhoneToDatabase();
    }

    //Method for adding phone to the database, triggered when the users
    public void addPhoneToDatabase() {
        final String phoneName = editPhoneName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        final Boolean phoneLockStatus = false;
        progressDialog.setMessage("Signing up...");
        progressDialog.show();
        firebaseAuthLogin.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String phoneId = firebaseAuthLogin.getCurrentUser().getUid();
                    Phone dbPhone = new Phone(phoneId, phoneName, phoneLockStatus);
                    DatabaseReference databaseCurrentUser = databasePhone.child(phoneId);
                    databaseCurrentUser.child("Name").setValue(dbPhone.getPhoneName());
                    databaseCurrentUser.child("PhoneLockStatus").setValue(dbPhone.getPhoneLockStatus());
                    progressDialog.dismiss();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                }
            }
        });
    }
}
