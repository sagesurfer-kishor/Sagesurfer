package com.modules.sos;

import android.app.Activity;
import android.content.Context;

import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 04-08-2017
 * Last Modified on 14-12-2017
 **/

/*
 * This class is for sos operation viz. attending, attended, not attending and forward
 */

class SosOperations_ {

    private static final String TAG = SosOperations_.class.getSimpleName();

    static int attending(int _id, int type, String method, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ATTENDING);
        requestMap.put(General.MSG_ID, "" + _id);
        requestMap.put(General.TYPE, "" + type);
        requestMap.put(General.METHOD, method);
        String response = null;
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SOS_OPERATION_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parseResponse(1, response, context);
    }

    static int notAttending(int _id, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.NOT_ATTENDING);
        requestMap.put(General.MSG_ID, "" + _id);

        String response = null;
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SOS_OPERATION_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parseResponse(2, response, context);
    }

    static int attended(int sos_id, String method, String comment, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ATTENDED);
        requestMap.put(General.MSG_ID, "" + sos_id);
        requestMap.put(General.TYPE, method);
        requestMap.put(General.COMMENT, comment);
        String response = null;
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SOS_OPERATION_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parseResponse(3, response, context);
    }

    static int forward(String sos_id, String message, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.FORWARD);
        requestMap.put(General.MSG_ID, sos_id);
        requestMap.put(General.MSG, message);
        String response = null;
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SOS_OPERATION_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parseResponse(4, response, context);
    }

    private static int parseResponse(int type, String response, Context context_) {
        //type > 1:attending;
        int status = 12;
        if (response != null) {
            JsonObject jsonObject = GetJson_.getJson(response);
            if (jsonObject.has(General.STATUS)) {
                status = jsonObject.get(General.STATUS).getAsInt();
            } else {
                status = 11;
            }
        }
        if (type == 1) {
            switch (status) {
                case 0:
                    ShowToast.networkError(context_);
                    break;
                case 1:
                    ShowToast.successful(context_.getResources().getString(R.string.successful), context_);
                    break;
                case 2:
                    ShowToast.successful(context_.getResources().getString(R.string.action_failed), context_);
                case 3:
                    ShowToast.toast("Already Attending", context_);
                    break;
                case 4:
                    ShowToast.toast("You don't have right for this", context_);
                    break;
            }
        }
        if (type == 2) {
            switch (status) {
                case 0:
                    ShowToast.networkError(context_);
                    break;
                case 1:
                    ShowToast.successful(context_.getResources().getString(R.string.successful), context_);
                    break;
                case 2:
                    ShowToast.successful(context_.getResources().getString(R.string.action_failed), context_);
                case 4:
                    ShowToast.toast("Already Done", context_);
                    break;
            }
        }
        if (type == 3) {
            switch (status) {
                case 0:
                    ShowToast.networkError(context_);
                    break;
                case 1:
                    ShowToast.successful(context_.getResources().getString(R.string.successful), context_);
                    break;
                case 2:
                    ShowToast.successful(context_.getResources().getString(R.string.action_failed), context_);
                case 3:
                    ShowToast.toast("Already Attended", context_);
                    break;
                case 4:
                    ShowToast.toast("You don't have right for this", context_);
                    break;
            }
        }

        if (type == 4) {
            switch (status) {
                case 0:
                    ShowToast.networkError(context_);
                    break;
                case 1:
                    ShowToast.successful(context_.getResources().getString(R.string.successful), context_);
                    break;
                case 2:
                    ShowToast.successful(context_.getResources().getString(R.string.action_failed), context_);
                case 4:
                    ShowToast.toast("Already Forwarded", context_);
                    break;
            }
        }
        return status;
    }
}
