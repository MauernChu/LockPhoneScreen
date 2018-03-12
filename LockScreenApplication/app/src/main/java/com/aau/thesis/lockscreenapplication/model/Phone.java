package com.aau.thesis.lockscreenapplication.model;

/**
 * Created by mette on 06/03/2018.
 */

public class Phone {
    private String phoneId;
    private String phoneName;
    private UnlockPhoneIdentifier unlockPhoneIdentifier;


    public Phone(String phoneId, String phoneName) {
        this.phoneId = phoneId;
        this.phoneName = phoneName;
        this.unlockPhoneIdentifier = unlockPhoneIdentifier;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public UnlockPhoneIdentifier getUnlockPhoneIdentifier() {
        return unlockPhoneIdentifier;
    }

    public void setUnlockPhoneIdentifier(UnlockPhoneIdentifier unlockPhoneIdentifier) {
        this.unlockPhoneIdentifier = unlockPhoneIdentifier;
    }
}
