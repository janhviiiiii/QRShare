package com.janhvi.qrshare.model;

import java.io.Serializable;

public class User implements Serializable {
    private long uid;
    private String email, username, password;

    // Constructor
    public User() {

    }
    public User(long uid, String email, String username, String password) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(long uid, String email, String username) {
        this.uid = uid;
        this.email = email;
        this.username = username;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
