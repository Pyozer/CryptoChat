package com.mjc.cryptochat;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bijou on 10/09/2017.
 */

public class Saloon {
    private int messageNumber;
    private String name;
    private String authorId;
    private String authorName;
    private String hint;

    public Saloon(int messageNumber, String name, String authorId, String authorName, String hint){
        super();
        this.messageNumber = messageNumber;
        this.name = name;
        this.authorId = authorId;
        this.authorName = authorName;
        this.hint = hint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("msgNb", messageNumber);
        result.put("authorId", authorId);
        result.put("authorName", authorName);
        result.put("name", name);
        result.put("hint", hint);

        return result;
    }
}
