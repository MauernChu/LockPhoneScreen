package com.aau.thesis.lockscreenapplication.data;

import com.aau.thesis.lockscreenapplication.data.listeners.PhoneLockStatusListener;

public class PhoneLockStatusService implements PhoneLockStatusListener {

    private static PhoneLockStatusService instance = null;
    protected PhoneLockStatusService() {
    }
    public static PhoneLockStatusService getInstance() {
        if(instance == null) {
            instance = new PhoneLockStatusService();
        }
        return instance;
    }

    private boolean isPhoneLocked;

    @Override
    public void PhoneLockStatusChanged(boolean isPhoneLocked) {
        this.isPhoneLocked = isPhoneLocked;
    }

    public boolean isPhoneLocked() {
        return isPhoneLocked;
    }

    public void setPhoneLocked(boolean phoneLocked) {
        isPhoneLocked = phoneLocked;
    }
}
