package com.aau.thesis.lockscreenapplication.model;


public class UnlockPhoneIdentifier {
    private String phoneId;
    private String unlockPhoneIdentifierID;
    private String unlockPhoneIdentifier;
    private String timestamp;

    public UnlockPhoneIdentifier(String unlockPhoneIdentifierID, String unlockPhoneIdentifier, String timestamp){
        this.unlockPhoneIdentifierID = unlockPhoneIdentifierID;
        this.unlockPhoneIdentifier = unlockPhoneIdentifier;
        this.timestamp = timestamp;
    }

    public String getUnlockPhoneIdentifier() {
        return unlockPhoneIdentifier;
    }

    public void setUnlockPhoneIdentifier(String unlockPhoneIdentifier) {
        this.unlockPhoneIdentifier = unlockPhoneIdentifier;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getUnlockPhoneIdentifierID() {
        return unlockPhoneIdentifierID;
    }

    public void setUnlockPhoneIdentifierID(String unlockPhoneIdentifierID) {
        this.unlockPhoneIdentifierID = unlockPhoneIdentifierID;
    }
}



