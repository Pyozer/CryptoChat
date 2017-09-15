package com.mjc.cryptochat.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bijou on 12/09/2017.
 */

public class Message {
    private String text;
    private String uid;
    private String authorName;
    private int time;

    public Message(){}
    public Message(String uid, String authorName, String text){
        super();
        this.uid = uid;
        this.authorName = authorName;
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

    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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
        result.put("auteur", authorName);
        result.put("text", text);

        return result;
    }
}
