package com.modules.selfcare;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.constant.Warnings;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/12/2018
 *         Last Modified on 4/12/2018
 */
/*
* Network call made from here for Self care operations like and comment
*/

public class SelfCareOperations {

    static int likeUnlike(String _id, String TAG, View view, Context _context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.LIKE_UNLIKE);
        requestMap.put(General.ID, _id);
        int result = 12;
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, _context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, _context, activity);
                if (response != null) {
                    if (Error_.oauth(response, _context) == 13) {
                        ShowSnack.viewWarning(view, Warnings.AUTHENTICATION_FAILED, _context);
                        return 13;
                    }
                    JsonObject object = GetJson_.getObject(response, Actions_.LIKE_UNLIKE);
                    if (object != null) {
                        if (object.has(General.STATUS)) {
                            result = object.get(General.STATUS).getAsInt();
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    static int comment(String _id, String message, String TAG, View view, Context _context, Activity activity) {
        int result = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.POST_COMMENT);
        requestMap.put(General.ID, _id);
        requestMap.put(General.COMMENT, message);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, _context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, _context, activity);
                if (response != null) {
                    if (Error_.oauth(response, _context) == 13) {
                        ShowSnack.viewWarning(view, Warnings.AUTHENTICATION_FAILED, _context);
                        return 13;
                    }
                    JsonObject object = GetJson_.getObject(response, Actions_.POST_COMMENT);
                    if (object != null) {
                        if (object.has(General.STATUS)) {
                            result = object.get(General.STATUS).getAsInt();
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static int share(String _id, String share_to_ids, String TAG, Context _context, Activity activity) {
        int result = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SHARE_FILE);
        requestMap.put(General.ID, _id);
        requestMap.put(General.SHARE_TO, share_to_ids);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, _context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, _context, activity);
                if (response != null) {
                    if (Error_.oauth(response, _context) == 13) {
                        return 13;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.SHARE_FILE);
                    if (jsonArray == null) {
                        result = 11;
                    } else {
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                        if (jsonObject.get(General.STATUS).getAsInt() == 1) {
                            result = jsonObject.get(General.STATUS).getAsInt();
                        } else {
                            result = jsonObject.get(General.STATUS).getAsInt();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
