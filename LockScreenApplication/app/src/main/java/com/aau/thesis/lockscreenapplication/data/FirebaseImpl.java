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
    private static FirebaseImpl _instance = null;
    private List<PhoneLockStatusListener> listeners = new ArrayList<PhoneLockStatusListener>();

    private DatabaseReference databasePhone;
    private DatabaseReference phoneLockStatus;
    private String phoneId;
    private boolean phoneLockStatusToString;
    private FirebaseAuth firebaseAuth;

    protected FirebaseImpl() {
    }

    public static FirebaseImpl getInstance() {
        if (_instance == null) {
            _instance = new FirebaseImpl();
        }
        return _instance;
    }

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
        //databasePhone = createDatabaseReferenceToPhone();
        phoneLockStatus = createDatabaseReferenceToPhoneLockStatus();
        firebaseAuth = createFirebaseAuth();
        phoneLockStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (firebaseAuth.getCurrentUser() != null) {
                    phoneLockStatusToString = dataSnapshot.getValue(Boolean.class);
                    // phoneId = getCurrentUserPhoneId();
                    // DataSnapshot phoneLockStatusSnapshot = dataSnapshot.child(phoneId).child("PhoneLockStatus");
                    //if (phoneLockStatusToString != null) {
                        //  isPhoneLocked = phoneLockStatusSnapshot.getValue(Boolean.class);
                        for (PhoneLockStatusListener listener : listeners) {
                            listener.PhoneLockStatusChanged(phoneLockStatusToString);
                        }
                    }
                }
            //}

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
