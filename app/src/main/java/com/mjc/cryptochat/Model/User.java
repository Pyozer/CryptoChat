package com.mjc.cryptochat.Model;

/**
 * Created by bijou on 10/09/2017.
 */

public class User {
    private String username;

    public User() {
        super();
    }

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
