package com.mjc.cryptochat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bijou on 12/09/2017.
 */

public class Message {
    private String text;
    private String authorId;
    private String authorName;
    private int time;

    public Message(String authorId, String authorName, String text){
        super();
        this.authorId = authorId;
        this.authorName = authorName;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", authorId);
        result.put("auteur", authorName);
        result.put("text", text);

        return result;
    }
}
