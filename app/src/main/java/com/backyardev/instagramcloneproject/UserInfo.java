package com.backyardev.instagramcloneproject;

public class UserInfo {
    private String Name,Uid;

    public UserInfo(String name, String uid) {
        this.Name = name;
        this.Uid = uid;
    }

    public UserInfo() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
