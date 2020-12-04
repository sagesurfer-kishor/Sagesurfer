package com.sagesurfer.tasks;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 10/9/2019.
 */
public class PerformJournalTask {
    private static final String TAG = PerformJournalTask.class.getSimpleName();

    public static void favourate(long jounal_id, int isFavUnFav, Context context, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.FAVE_UNFAV);
        requestMap.put("id", "" + jounal_id);
        requestMap.put("is_fav_unfav", "" + isFavUnFav);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonFavUnFav = jsonObject.getAsJsonObject(Actions_.FAVE_UNFAV);
                    if (jsonFavUnFav.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, jsonFavUnFav.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
