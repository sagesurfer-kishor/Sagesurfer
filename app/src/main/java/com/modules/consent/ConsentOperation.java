package com.modules.consent;

import android.app.Activity;

import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 10-08-2017
 *         Last Modified on 16-11-2017
 */


class ConsentOperation {

    private static final String TAG = ConsentOperation.class.getSimpleName();

    static String getUrl(String action, String file_id, Activity activity) {
        String url = null;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.ID, file_id);

        String request_url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CONSENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, request_url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(request_url, requestBody, TAG, activity, activity);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has(action)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(action);
                    JSONObject object = jsonArray.getJSONObject(0);
                    if (object.getInt(General.STATUS) == 1) {
                        return object.getString(General.PATH);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
