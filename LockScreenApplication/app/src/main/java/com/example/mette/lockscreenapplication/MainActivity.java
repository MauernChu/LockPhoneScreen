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

public class MainActivity extends Activity {
    Button unlock_screen;
    EditText enter_code;
    TextView display_success;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Set up our Lockscreen
            makeFullScreen();
            startService(new Intent(this,LockScreenService.class));

            setContentView(R.layout.activity_main);

            enter_code = (EditText) findViewById(R.id.enter_code);
            display_success = (TextView) findViewById(R.id.display_success);
        }

        /**
         * A simple method that sets the screen to fullscreen.  It removes the Notifications bar,
         *   the Actionbar and the virtual keys (if they are on the phone)
         */
    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
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

    public void unlockScreen(View view) {
        //Instead of using finish(), this totally destroys the process
        String password = Credentials.password();
        String enterCodeToString = enter_code.getText().toString();
        if(enterCodeToString.equals(password)){
            display_success.setText("Correct code!");
            android.os.Process.killProcess(android.os.Process.myPid());
        }else{
            display_success.setText("Wrong code!");
        }
    }
}