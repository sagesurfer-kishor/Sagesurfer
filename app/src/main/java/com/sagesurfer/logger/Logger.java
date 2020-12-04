package com.sagesurfer.logger;

import android.content.Context;
import android.util.Log;

import com.storage.preferences.Preferences;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class Logger {

    public static void debug(String tag, String message, Context _context) {
        Preferences.initialize(_context);
        if (Preferences.contains("debug") && Preferences.get("debug").equalsIgnoreCase("1")) {
            Log.d(tag, message);
        }
    }

    public static void info(String tag, String message, Context _context) {
        Preferences.initialize(_context);
        if (Preferences.contains("info") && Preferences.get("info").equalsIgnoreCase("1")) {
            Log.i(tag, message);
        }
    }

    public static void verbose(String tag, String message, Context _context) {
        Preferences.initialize(_context);
        if (Preferences.contains("verbose") && Preferences.get("info").equalsIgnoreCase("1")) {
            Log.v(tag, message);
        }
    }


    public static void error(String tag, String message, Context _context) {
        Preferences.initialize(_context);
        if (Preferences.contains("error") && Preferences.get("error").equalsIgnoreCase("1")) {
            Log.e(tag, message);
        }
    }
}
