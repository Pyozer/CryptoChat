package com.mjc.cryptochat.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Thecr on 18/09/2017.
 */

public class PrefManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public PrefManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public String getSaloonHintSaved(String saloontKey) {
        return preferences.getString(saloontKey, "");
    }

    public void saveHintOfSaloon(String saloontKey, String supposedHint) {
        editor.putString(saloontKey, supposedHint);
        editor.apply();
    }
}
