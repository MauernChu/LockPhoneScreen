package com.example.mette.lockscreenapplication.model;

/**
 * Created by mette on 06/03/2018.
 */

public class Phone {
    String phoneId;
    String phoneName;
    Boolean lockStatus;

    public Phone(String phoneId, String phoneName, Boolean lockStatus) {
        this.phoneId = phoneId;
        this.phoneName = phoneName;
        this.lockStatus = lockStatus;
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

    public Boolean getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(Boolean lockStatus) {
        this.lockStatus = lockStatus;
    }
}
