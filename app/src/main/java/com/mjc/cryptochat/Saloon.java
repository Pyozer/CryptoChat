package com.mjc.cryptochat;

/**
 * Created by bijou on 10/09/2017.
 */

public class Saloon {
    private String name;
    private String hint;
    public Saloon(String name, String hint){
        super();
        this.name = name;
        this.hint = hint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
