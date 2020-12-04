package com.sagesurfer.tasks;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.MessageBoard_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 14-08-2017
 * Last Modified on 15-12-2017
 */

public class PerformDeleteTask {

    public static int deleteAlert(String id, String type, String is_delete, String alert_id, String TAG, Context context, Activity activity) {
        int result = 12;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ID, id);
        requestMap.put(General.TYPE, type);
        requestMap.put(General.ALERT_ID, alert_id);
        requestMap.put(General.IS_DELETE, is_delete);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ALERT_DELETE_URL;
        Log.e("deleteAlert Request: ", String.valueOf(requestMap));
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                Log.e("deleteAlert Response: ",response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject == null) {
                        result = 11;
                    } else {
                        if (jsonObject.has(General.STATUS)) {
                            result = jsonObject.get(General.STATUS).getAsInt();
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

    public static int deleteMessageBoard(String id, String action, String TAG, Context context, Activity activity) {
        int result = 12;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.ID, id);
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    ArrayList<MessageBoard_> tempMessageBoardArrayList = Alerts_.parseMessageBoard(response, action, activity.getApplicationContext(), TAG);
                    if (tempMessageBoardArrayList.size() > 0) {
                        if (tempMessageBoardArrayList.get(0).getStatus() == 1) {
                            result = 1;
                        } else {
                            result = tempMessageBoardArrayList.get(0).getStatus();
                        }
                    } else {
                        result = 12;
                    }
                    /*JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject == null) {
                        result = 11;
                    } else {
                        if (jsonObject.has(General.STATUS)) {
                            result = jsonObject.get(General.STATUS).getAsInt();
                        } else {
                            result = 11;
                        }//{"delete_messageboard":[{"msg":"Message Board deleted succesfully.","status":1}]}
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
