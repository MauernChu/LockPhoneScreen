package com.aau.thesis.lockscreenapplication.model;


public class UnlockPhoneList {
    private String phoneId;
    private String unlockPhoneListID;
    private String unlockPhoneReason;
    private String timestamp;

    public UnlockPhoneList(String unlockPhoneIdentifierID, String unlockPhoneIdentifier, String timestamp){
        this.unlockPhoneListID = unlockPhoneIdentifierID;
        this.unlockPhoneReason = unlockPhoneIdentifier;
        this.timestamp = timestamp;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getUnlockPhoneListID() {
        return unlockPhoneListID;
    }

    public void setUnlockPhoneListID(String unlockPhoneListID) {
        this.unlockPhoneListID = unlockPhoneListID;
    }

    public String getUnlockPhoneReason() {
        return unlockPhoneReason;
    }

    public void setUnlockPhoneReason(String unlockPhoneReason) {
        this.unlockPhoneReason = unlockPhoneReason;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}



