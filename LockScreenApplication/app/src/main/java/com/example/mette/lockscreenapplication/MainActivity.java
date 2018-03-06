package com.example.mette.lockscreenapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mette.lockscreenapplication.data.Credentials;
import com.example.mette.lockscreenapplication.model.Phone;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {
    EditText enter_code;
    TextView display_success;

    DatabaseReference databasePhone;

    String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up our Lockscreen
        makeFullScreen();
        startService(new Intent(this, LockScreenService.class));

        setContentView(R.layout.activity_main);

        databasePhone = FirebaseDatabase.getInstance().getReference("Phone");

        enter_code = (EditText) findViewById(R.id.enter_code);
        display_success = (TextView) findViewById(R.id.display_success);
    }

    /**
     * A simple method that sets the screen to fullscreen.  It removes the Notifications bar,
     * the Actionbar and the virtual keys (if they are on the phone)
     */
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

  /*  @Override
    protected void onStart() {
        super.onStart();
        databasePhone.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot codeSnapShot : dataSnapshot.getChildren()) {
                    code = codeSnapShot.getValue(String.class);
                    display_success.setText(code);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }

    public void unlockScreen(View view) throws InterruptedException {
        //Instead of using finish(), this totally destroys the process
        String password = Credentials.password();
        String enterCodeToString = enter_code.getText().toString();
        if (enterCodeToString.equals(password)) {
            display_success.setText("Correct code!");
            addPhoneUnlock();
            Thread.sleep(5);
            shutDownApp();
        } else {
            display_success.setText("wrong code!");
        }
    }

    private void addPhoneUnlock(){
        String id = databasePhone.push().getKey();
        String name = "Mette";
        Boolean unlockPhone = true;

        Phone unlockedPhone = new Phone(id, name, unlockPhone);
        databasePhone.child(id).setValue(unlockedPhone);
    }

    public void shutDownApp(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}