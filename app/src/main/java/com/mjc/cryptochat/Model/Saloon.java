package com.mjc.cryptochat.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bijou on 10/09/2017.
 */

public class Saloon {

    private int msgNb;
    private String name;
    private String authorId;
    private String authorName;
    private String hint;
    private String msgDefaultCrypt;

    public Saloon() {
    }

    public Saloon(int msgNb, String name, String authorId, String authorName, String hint, String msgDefaultCrypt) {
        super();
        this.msgNb = msgNb;
        this.name = name;
        this.authorId = authorId;
        this.authorName = authorName;
        this.hint = hint;
        this.msgDefaultCrypt = msgDefaultCrypt;
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

    public int getMsgNb() {
        return msgNb;
    }

    public void setMsgNb(int msgNb) {
        this.msgNb = msgNb;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getMsgDefaultCrypt() {
        return msgDefaultCrypt;
    }

    public void setMsgDefaultCrypt(String msgDefaultCrypt) {
        this.msgDefaultCrypt = msgDefaultCrypt;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("msgNb", msgNb);
        result.put("authorId", authorId);
        result.put("authorName", authorName);
        result.put("name", name);
        result.put("hint", hint);
        result.put("msgDefaultCrypt", msgDefaultCrypt);

        return result;
    }
}
