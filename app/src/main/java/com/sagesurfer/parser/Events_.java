package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.Location_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 7/2/2018.
 */

public class Events_ {
    public static ArrayList<Location_> parseGetLocations(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Location_> locationsArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                return locationsArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Location_>>() {
            }.getType();
            locationsArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        }
        return locationsArrayList;
    }

    public static ArrayList<Location_> parseAddLocation(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Location_> locationArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Location_ locationItem_ = new Location_();
                locationItem_.setStatus(12);
                locationArrayList.add(locationItem_);
                return locationArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Location_ locationItem_ = new Location_();
                locationItem_.setStatus(13);
                locationArrayList.add(locationItem_);
                return locationArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Location_ locationItem_ = new Location_();
                locationItem_.setStatus(2);
                locationArrayList.add(locationItem_);
                return locationArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Location_>>() {
                }.getType();
                locationArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Location_ locationItem_ = new Location_();
                locationItem_.setStatus(11);
                locationArrayList.add(locationItem_);
                return locationArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return locationArrayList;
    }
}
