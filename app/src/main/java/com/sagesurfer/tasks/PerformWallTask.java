package com.sagesurfer.tasks;

import android.app.Activity;
import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.constant.Actions_;
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
 * Created on 12-07-2017
 * Last Modified on 15-12-2017
 */

public class PerformWallTask {

    private static final String TAG = PerformWallTask.class.getSimpleName();

    public static int[] like(int feed_id, Context context, Activity activity) {
        int[] status = {12, 0};
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.LIKE_UNLIKE);
        requestMap.put("feed_id", "" + feed_id);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.WALL_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 0) {
                        JsonArray jsonArray = GetJson_.getArray(response, General.LIKE);
                        if (jsonArray == null) {
                            status[0] = 11;
                        } else {
                            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                            if (jsonObject.get(General.STATUS).getAsInt() == 1) {
                                status[0] = jsonObject.get(General.STATUS).getAsInt();
                                status[1] = jsonObject.get(General.COUNT).getAsInt();
                            } else {
                                status[0] = jsonObject.get(General.STATUS).getAsInt();
                            }
                        }
                    } else {
                        status[0] = 13;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

}
