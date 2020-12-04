package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.motivation.model.ToolKitData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kailash Karankal on 6/8/2019.
 */
public class ToolkitParser_ {

    public static ArrayList<ToolKitData> parseGetToolkit(String response, String jsonName, Context _context, String TAG) {
        ArrayList<ToolKitData> motivationArrayList = new ArrayList<>();
        try {
            if (response == null) {
                ToolKitData motivationItem_ = new ToolKitData();
                motivationItem_.setStatus(12);
                motivationArrayList.add(motivationItem_);
                return motivationArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                ToolKitData motivationItem_ = new ToolKitData();
                motivationItem_.setStatus(13);
                motivationArrayList.add(motivationItem_);
                return motivationArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                ToolKitData motivationItem_ = new ToolKitData();
                motivationItem_.setStatus(2);
                motivationArrayList.add(motivationItem_);
                return motivationArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ToolKitData>>() {
                }.getType();
                motivationArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                ToolKitData motivationItem_ = new ToolKitData();
                motivationItem_.setStatus(11);
                motivationArrayList.add(motivationItem_);
                return motivationArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return motivationArrayList;
    }
}
