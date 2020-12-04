package com.sagesurfer.tasks;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Login_;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class SyncUserData {

    public static void sync(Context _context, String className, Activity activity) {
        if (Preferences.contains(General.LAST_UPDATED) && Preferences.get(General.LAST_UPDATED) != null) {
            if (System.currentTimeMillis() < Long.parseLong(Preferences.get(General.LAST_UPDATED))) {
                return;
            }
        }
        if (Preferences.contains(General.DOMAIN) && Preferences.get(General.DOMAIN) != null) {
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put(General.ACTION, Actions_.GET_USER_DATA);
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

            String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USER_SYNC;
            RequestBody requestBody = NetworkCall_.make(requestMap, url, className, _context, activity);
            if (requestBody != null) {
                try {
                    String response = NetworkCall_.post(url, requestBody, className, _context, activity);
                    Log.e("syncResponse",response);
                    if (response != null && !response.equalsIgnoreCase("13")) {
                        Login_.userInfoParser(response, Actions_.GET_USER_DATA, _context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
