package com.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sagesurfer.models.Counters_;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

/*
 * This file contains Shared preferences for application
 * Used to save basic user information and counter json
 */

public class Preferences {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static void initialize(Context con) {
        if (null == preferences) {
            preferences = PreferenceManager.getDefaultSharedPreferences(con);
        }
        if (null == editor) {
            editor = preferences.edit();
            editor.apply();
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

    public static void save(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void save(String key, Integer value) {
        save(key, String.valueOf(value));
    }

    public static void save(String key, Counters_ counters_) {
        Gson gson = new Gson();
        String json = gson.toJson(counters_);
        editor.putString(key, json);
        editor.commit();
    }

    public static void save(String key, Long value) {
        save(key, String.valueOf(value));
    }

    public static String get(String key) {
        return preferences.getString(key, "");
    }


    public static boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static Boolean contains(String key) {
        return preferences.contains(key);
    }

    // Fetch and parse counters json stored in SharedPreference
    public static Counters_ getCounters(String key) {
        if (preferences.contains(key)) {
            Gson gson = new Gson();
            String json = preferences.getString(key, null);
            return gson.fromJson(json, Counters_.class);
        }
        return null;
    }

    public static void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }
}
