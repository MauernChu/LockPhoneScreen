package com.aau.thesis.lockscreenapplication.data;

import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseImpl implements DatabaseInterface {
    private List<PhoneLockStatusListener> listeners = new ArrayList();

    DatabaseReference databasePhone;
    DatabaseReference phoneLockStatus;
    String phoneId;
    boolean isPhoneLocked = false;
    FirebaseAuth firebaseAuth;

    /**
     * Method for creating DatabaseReferences to database
     *
     * @return DatabaseReference
     */
    @Override
    public DatabaseReference createDatabaseReferenceToPhoneLockStatus() {
        return createDatabaseReferenceByName("PhoneLockStatus");
    }

    @Override
    public DatabaseReference createDatabaseReferenceToCode() {
        return createDatabaseReferenceByName("Code");
    }

    @Override
    public DatabaseReference createDatabaseReferenceToTotal() {
        return createDatabaseReferenceByName("Total");
    }

    @Override
    public DatabaseReference createDatabaseReferenceToPhone() {
        return createDatabaseReferenceByName("Phone");
    }

    @Override
    public DatabaseReference createDatabaseReferenceToUnlockIdentifier() {
        return createDatabaseReferenceByName("UnlockIdentifier");
    }

    @Override
    public DatabaseReference createDatabaseReferenceToCodeEntered() {
        return createDatabaseReferenceByName("CodeEntered");
    }

    @Override
    public FirebaseAuth createFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return createFirebaseAuth().getCurrentUser();
    }

    @Override
    public String getCurrentUserPhoneId() {
        if (createFirebaseAuth().getCurrentUser() != null) {
            return getCurrentUser().getUid();
        } else {
            return null;
        }
    }

    @Override
    public void listenToPhoneLockStatus() {
        databasePhone = createDatabaseReferenceToPhone();
        phoneLockStatus = createDatabaseReferenceToPhoneLockStatus();
        firebaseAuth = createFirebaseAuth();
        databasePhone.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneId = getCurrentUserPhoneId();
                isPhoneLocked = dataSnapshot.child(phoneId).child("PhoneLockStatus").getValue(Boolean.class);
                PhoneLockStatusService.getInstance().setPhoneLocked(isPhoneLocked);
                for(PhoneLockStatusListener listener : listeners) {
                    listener.PhoneLockStatusChanged(isPhoneLocked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addListener(PhoneLockStatusListener listener) {
        listeners.add(listener);
    }


    private static DatabaseReference createDatabaseReferenceByName(String code) {
        return FirebaseDatabase.getInstance().getReference(code);
    }

}




