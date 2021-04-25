package com.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

/*
 * This file contains Shared preferences for Add Goal procedure to save it in private mode
 * Data is saved to share and use between all three stage of goal creation
 */

public class AddGoalPreferences {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static void initialize(Context con) {
        if (null == preferences) {
            preferences = con.getSharedPreferences("add_goal", Context.MODE_PRIVATE);
        }
        if (null == editor) {
            editor = preferences.edit();
            editor.apply();
        }
    }

    public static void clear(String className) {
        editor.clear();
        editor.commit();
    }

    public static void save(String key, String value, String className) {
        editor.putString(key, value);
        editor.commit();
    }

    public static void save(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key) {
        return preferences.getBoolean(key, true);
    }

    public static String get(String key) {
        return preferences.getString(key, null);
    }

    public static Boolean contains(String key) {
        return preferences.contains(key);
    }

    public static void removeKey(String key, String className) {
        editor.remove(key);
        editor.commit();
    }
}
