package com.sagesurfer.tasks;

import android.app.Activity;
import android.content.Context;

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
 * Created on 15-09-2017
 * Last Modified on 15-12-2017
 */

public class PerformReadTask {

    public static int readAlert(String id, String type, String TAG, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ID, id);
        requestMap.put(General.TYPE, type);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_READ_ALERTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        return 13;
                    }
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        return jsonObject.get(General.STATUS).getAsInt();
                    }
                } else {
                    return 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 11;
    }

    public static int readAlert_One(String id, String type, String TAG, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ID, id);
        requestMap.put(General.TYPE, type);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_READ_ALERTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        return 13;
                    }
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        return jsonObject.get(General.STATUS).getAsInt();
                    }
                } else {
                    return 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 11;
    }


    public static int readAlert_Two(String id, String alertId, String type, String TAG, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ID, id);
        requestMap.put(General.TYPE, type);
        requestMap.put(General.ALERT_ID, alertId);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_READ_ALERTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        return 13;
                    }
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        return jsonObject.get(General.STATUS).getAsInt();
                    }
                } else {
                    return 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 11;
    }


    public static int messageReadUnRead(String id, String type, String TAG, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TYPE, "postcard");
        requestMap.put("Ttype", type);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.ID, id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_READ_ALERTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        return 13;
                    }
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        return jsonObject.get(General.STATUS).getAsInt();
                    }
                } else {
                    return 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 11;
    }

}
