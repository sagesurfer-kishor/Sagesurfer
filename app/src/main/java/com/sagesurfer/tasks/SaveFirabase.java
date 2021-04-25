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

import static com.sagesurfer.constant.General.FIREBASE_ID;

/**
 * @author Kailash Karankal
 */

public class SaveFirabase {

    public static void save(Context _context, String id, String className, Activity activity) {
        if (Preferences.contains(General.DOMAIN) && Preferences.get(General.DOMAIN) != null) {
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put(FIREBASE_ID, id);

            String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FIREBASE_REGISTER;
            RequestBody requestBody = NetworkCall_.make(requestMap, url, className, _context, activity);
            if (requestBody != null) {
                try {
                    String response = NetworkCall_.post(url, requestBody, className, _context, activity);
                    if (response != null) {
                        if (Error_.oauth(response, _context) != 13) {
                            JsonArray jsonArray = GetJson_.getArray(response, "firebase");
                            if (jsonArray != null) {
                                JsonObject object = jsonArray.get(0).getAsJsonObject();
                                if (object.has(General.STATUS)) {
                                    if (object.get(General.STATUS).getAsInt() == 1) {
                                        Preferences.save("regId_save", true);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
