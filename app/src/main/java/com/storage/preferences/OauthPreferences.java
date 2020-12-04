package com.storage.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

/*
 * This file contains Shared preferences for OAuth tokens to save it in private mode
 */

public class OauthPreferences {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    @SuppressLint("ApplySharedPref")
    public static void initialize(Context _context) {
        if (null == preferences) {
            preferences = _context.getApplicationContext()
                    .getSharedPreferences("oauth", Context.MODE_PRIVATE);
        }
        if (null == editor) {
            editor = preferences.edit();
            editor.commit();
        }
    }

    public static void clear() {
        editor.clear();
        editor.commit();
    }

    public static void save(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static void save(String key, Integer value) {
        save(key, String.valueOf(value));
    }

    public static void save(String key, Long value) {
        save(key, String.valueOf(value));
    }

    public static String get(String key) {
        return preferences.getString(key, null);
    }

    public static Boolean contains(String key) {
        return preferences.contains(key);
    }

    public static void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }
}
