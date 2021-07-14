package com.sagesurfer.tasks;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.icons.GetHomeMenuIcon;
import com.sagesurfer.models.CometChatTeamMembers_;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Users_;
import com.storage.preferences.Preferences;
import com.utils.AppLog;

import java.util.ArrayList;
import java.util.HashMap;


import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 20-07-2017
 * Last Modified on 15-12-2017
 */


public class PerformGetUsersTask {

    public static ArrayList<Friends_> get(String action, String shared_to_ids, Context context, String tag, Activity activity) {

        ArrayList<Friends_> usersList = new ArrayList<>();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        if (action.equals(Actions_.MY_GROUP_FRIENDS)){
            requestMap.put(General.GROUP_ID,shared_to_ids);
        }else {
            requestMap.put(General.SHARED_TO_IDS, shared_to_ids);
        }
        requestMap.put(General.USER_ID,Preferences.get(General.USER_ID));
        Log.e("RequestMap",""+requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USERS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                Log.e("Response",""+response);
                if (response != null) {
                    usersList = Users_.parse(response, action, context, tag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return usersList;
    }

    public static ArrayList<CometChatTeamMembers_> getCometChatTeamMembers(String action, Context context, String tag, Activity activity) {

        ArrayList<CometChatTeamMembers_> teamMemberList = new ArrayList<>();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));
        requestMap.put(General.CODE, Preferences.get(General.DOMAIN_CODE));
        requestMap.put(General.ISFORTEAMCHAT, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USERS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                AppLog.e("getCometChatTeamMembers response",response);
                if (response != null) {
                    teamMemberList = Users_.parseCometChatTeamMembers(response, action, context, tag);

                    JsonArray jsonArray = GetJson_.getArray(response, action);
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                            if (jsonObject.get(General.STATUS).getAsInt() == 1) {

                            }
                        }
                    }
                }
            } catch (NoClassDefFoundError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return teamMemberList;
    }
}
