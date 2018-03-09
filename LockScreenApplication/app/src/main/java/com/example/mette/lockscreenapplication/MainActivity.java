package com.example.mette.lockscreenapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mette.lockscreenapplication.helper.SaveSharedPreference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {
    DatabaseReference databaseHomeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("test");
        super.onCreate(savedInstanceState);

        //Set up our Lockscreen
        makeFullScreen();
        startService(new Intent(this, LockScreenService.class));

        databaseHomeKey = FirebaseDatabase.getInstance().getReference("HomeKey");

        if (SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
            setContentView(R.layout.activity_main);
        } else {

        }



      /*  HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                String idHomeKey = databaseHomeKey.push().getKey();
                Boolean homeKeyPushed = true;
                databaseHomeKey.child(idHomeKey).setValue(homeKeyPushed);
            }
            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();*/
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

    @Override
    public void onBackPressed() {
        return; //Do nothing!
    }

    public void goToEnterCodeActivity(View view) throws InterruptedException {
        Intent intent = new Intent(this, EnterCodeActivity.class);
        startActivity(intent);
    }


    public void shutDownApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

  /*  private void addPhoneUnlock() {
        String id = databasePhone.push().getKey();
        String name = "Mette";
        Boolean unlockPhone = true;

        Phone unlockedPhone = new Phone(id, name, unlockPhone);
        databasePhone.child(id).setValue(unlockedPhone);
    }*/


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

  /*  public static Phone setTestPhone(String phoneId, String phoneName, boolean lockstatus){
        Phone testPhone = new Phone(phoneId, phoneName, lockstatus);
        Boolean lockPhone = testPhone.getLockStatus();
        return Phone;
    }*/
}