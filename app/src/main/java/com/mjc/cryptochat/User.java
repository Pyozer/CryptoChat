package com.mjc.cryptochat;

/**
 * Created by bijou on 10/09/2017.
 */

public class User {
    public String email;
    public String name;

    public User(){
        super();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
