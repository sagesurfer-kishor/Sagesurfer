package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.HomeRecentUpdates_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 12/24/2018.
 */

public class HomeParser_ {
    public static ArrayList<HomeRecentUpdates_> parseHomeRecentUpdates(String response, String jsonName, Context _context, String TAG) {
        ArrayList<HomeRecentUpdates_> recentUpdatesArrayList = new ArrayList<>();
        try {
            if (response == null) {
                HomeRecentUpdates_ recentUpdatesItem_ = new HomeRecentUpdates_();
                recentUpdatesItem_.setStatus(12);
                recentUpdatesArrayList.add(recentUpdatesItem_);
                return recentUpdatesArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                HomeRecentUpdates_ recentUpdatesItem_ = new HomeRecentUpdates_();
                recentUpdatesItem_.setStatus(13);
                recentUpdatesArrayList.add(recentUpdatesItem_);
                return recentUpdatesArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                HomeRecentUpdates_ recentUpdatesItem_ = new HomeRecentUpdates_();
                recentUpdatesItem_.setStatus(2);
                recentUpdatesArrayList.add(recentUpdatesItem_);
                return recentUpdatesArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<HomeRecentUpdates_>>() {
                }.getType();
                recentUpdatesArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                HomeRecentUpdates_ recentUpdatesItem_ = new HomeRecentUpdates_();
                recentUpdatesItem_.setStatus(11);
                recentUpdatesArrayList.add(recentUpdatesItem_);
                return recentUpdatesArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return recentUpdatesArrayList;
    }
}
