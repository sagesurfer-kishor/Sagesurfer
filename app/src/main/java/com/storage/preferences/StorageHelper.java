package com.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;


public class StorageHelper {

    public static void setTOKEN(Context context, String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(ConstantData.PREF_FCM);
        editor.putString(ConstantData.PREF_FCM, token);
        editor.apply();

    }

    public static String geTOKEN(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = "";
        if (preferences.contains(ConstantData.PREF_FCM)) {
            value = preferences.getString(ConstantData.PREF_FCM, "0");
            return value;
        }
        return value;
    }
    public static void removeAllPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ConstantData.PREF_FCM, "");
        editor.apply();

    }


}
