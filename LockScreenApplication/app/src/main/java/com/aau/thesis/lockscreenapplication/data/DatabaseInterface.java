package com.aau.thesis.lockscreenapplication.data;

import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public interface DatabaseInterface {

    DatabaseReference createDatabaseReferenceToPhoneLockStatus();

    DatabaseReference createDatabaseReferenceToCode();

    DatabaseReference createDatabaseReferenceToTotal();

    DatabaseReference createDatabaseReferenceToPhone();

    DatabaseReference createDatabaseReferenceToUnlockIdentifier();

    DatabaseReference createDatabaseReferenceToCodeEntered();

    FirebaseAuth createFirebaseAuth();

    FirebaseUser getCurrentUser();

    String getCurrentUserPhoneId();

    void listenToPhoneLockStatus();

    void addListener(PhoneLockStatusListener listener);
}
