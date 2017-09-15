package com.mjc.cryptochat.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bijou on 12/09/2017.
 */

public class Message {
    private String text;
    private String uid;
    private String auteur;
    private int time;

    public Message(){}
    public Message(String uid, String auteur, String text){
        super();
        this.uid = uid;
        this.auteur = auteur;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuteur() {
        return this.auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("auteur", auteur);
        result.put("text", text);

        return result;
    }
}
