package com.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 16/03/2018
 *         Last Modified on
 */

/*
 * This file contains Shared preferences for Sign up pages
 * Shared preferences are created in private mode and wil be wiped out once goal is created to module/app get closed
 */

public class SignUpPreferences {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    // initialize shared preferences
    public static void initialize(Context con) {
        if (null == preferences) {
            preferences = con.getSharedPreferences("add_goal", Context.MODE_PRIVATE);
        }
        if (null == editor) {
            editor = preferences.edit();
            editor.apply();
        }
    }

    // clear shared preferences
    public static void clear(String className) {
        editor.clear();
        editor.commit();
    }

    // save string value shared preferences
    public static void save(String key, String value, String className) {
        editor.putString(key, value);
        editor.commit();
    }

    // Get string value from shared preferences with key
    public static String get(String key) {
        return preferences.getString(key, null);
    }

    // Get boolean value from shared preferences with key
    public static Boolean contains(String key) {
        return preferences.contains(key);
    }

    // remove value from shared preferences with key
    public static void removeKey(String key, String className) {
        editor.remove(key);
        editor.commit();
    }
}
