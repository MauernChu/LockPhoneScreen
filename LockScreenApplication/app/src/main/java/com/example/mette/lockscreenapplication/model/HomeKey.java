package com.example.mette.lockscreenapplication.model;

import java.sql.Date;
import java.util.List;


public class HomeKey {
    private List<Boolean> homeKeyLocked;
    private String homeKeyId;
    private Date timestamp;
    private Phone homeKeyPhone;


    public HomeKey(List<Boolean> homeKeyLocked, String homeKeyId) {
        this.homeKeyLocked = homeKeyLocked;
        this.homeKeyId = homeKeyId;
    }

    public void setHomeKeyLocked(List<Boolean> homeKeyLocked) {
        this.homeKeyLocked = homeKeyLocked;
    }

    public List<Boolean> getHomeKeyLocked() {
        return homeKeyLocked;
    }

    public void setHomeKeyId(String homeKeyId) {
        this.homeKeyId = homeKeyId;
    }

    public String getHomeKeyId() {
        return homeKeyId;
    }
}




