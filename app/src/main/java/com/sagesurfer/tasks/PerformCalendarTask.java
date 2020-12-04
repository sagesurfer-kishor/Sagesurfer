package com.sagesurfer.tasks;

import android.app.Activity;
import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 17-08-2017
 * Last Modified on 15-12-2017
 */

public class PerformCalendarTask {

    public static int inviteAction(String action, String id, String TAG, Context context, Activity activity) {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put("event_id", id);
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        return 13;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, action);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                        } else {
                            status = 11;
                        }
                    } else {
                        status = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }
}
