package com.modules.selfcare;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 22-09-2017
 *         Last Modified on 14-12-2017
 */

/*
* This class contain all network call for self care operations viz. delete,accept,decline
*/

class CareUploaderActions {

    public static int delete(String action, long id, String TAG, Context context, View view, Activity activity) {
        int status = 12;
        String delete_type = "pending";
        if (action.equalsIgnoreCase(Actions_.SHARED)) {
            delete_type = "shared";
        }

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE);
        requestMap.put(General.TYPE, delete_type);
        requestMap.put(General.ID, "" + id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        status = 13;
                        showResponses(status, view, context);
                        return 13;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.DELETE);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                        } else {
                            status = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, view, context);
        return status;
    }

    static int reviewActionDecline(String owner_id, long id, String TAG, Context context, View view, Activity activity) {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DECLINE);
        requestMap.put(General.OWNER_ID, "" + owner_id);
        requestMap.put(General.ID, "" + id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        status = 13;
                        showResponses(status, view, context);
                        return 13;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.DECLINE);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                        } else {
                            status = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, view, context);
        return status;
    }

    static int reviewActionApprove(String owner_id, long id, String TAG, Context context, View view, Activity activity) {
        int status = 12;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.APPROVE);
        requestMap.put(General.OWNER_ID, "" + owner_id);
        requestMap.put(General.ID, "" + id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        status = 13;
                        showResponses(status, view, context);
                        return 13;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.APPROVE);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                        } else {
                            status = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, view, context);
        return status;
    }

    private static void showResponses(int status, View view, Context context) {
        String message;
        if (status == 1) {
            message = context.getResources().getString(R.string.successful);
        } else {
            status = 2;
            message = context.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, context);
    }
}
