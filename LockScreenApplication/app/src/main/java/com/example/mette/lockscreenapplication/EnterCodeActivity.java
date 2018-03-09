package com.example.mette.lockscreenapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mette.lockscreenapplication.data.Credentials;
import com.example.mette.lockscreenapplication.model.Phone;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EnterCodeActivity extends Activity {
    EditText enter_code;
    TextView display_success;

    String phoneId;
    String phoneName;
    boolean lockstatus;

    String code;

    DatabaseReference databasePhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        makeFullScreen();

        databasePhone = FirebaseDatabase.getInstance().getReference("Phone");

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

    public void unlockScreen(View view) throws InterruptedException {
        //Instead of using finish(), this totally destroys the process
        String password = Credentials.password();
        String enterCodeToString = enter_code.getText().toString();
        if (enterCodeToString.equals(password)) {
            display_success.setText("Correct code!");
            addPhoneUnlock();
            //Thread.sleep(5);
            shutDownApp();
        } else {
            display_success.setText("wrong code!");
        }
    }

    public void shutDownApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void addPhoneUnlock() {
        String id = databasePhone.push().getKey();
        String name = "Mette";
        Boolean unlockPhone = true;

        Phone unlockedPhone = new Phone(id, name, unlockPhone);
        databasePhone.child(id).setValue(unlockedPhone);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
