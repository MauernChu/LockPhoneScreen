package com.example.mette.lockscreenapplication.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mette on 23/02/2018.
 */

public abstract class Credentials {
    private String password;

    DatabaseReference databaseCode;

    public void getGeneratedCode(){
        databaseCode = FirebaseDatabase.getInstance().getReference("Code");
    }

    public static String password(){
        String password = "1234";
        return password;
    }
}
