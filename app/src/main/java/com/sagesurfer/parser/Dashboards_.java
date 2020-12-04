package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.dashboard.Note_;
import com.sagesurfer.constant.Actions_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 04-10-2017
 * Last Modified 04-10-2017
 */

public class Dashboards_ {

    public static ArrayList<Note_> parseNotes(String response, Context _context, String TAG) {
        ArrayList<Note_> noteArrayList = new ArrayList<>();
        if (response == null) {
            Note_ spamItem_ = new Note_();
            spamItem_.setStatus(12);
            noteArrayList.add(spamItem_);
            return noteArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Note_ spamItem_ = new Note_();
            spamItem_.setStatus(13);
            noteArrayList.add(spamItem_);
            return noteArrayList;
        }
        if (Error_.noData(response, Actions_.STICKY_NOTES, _context) == 2) {
            Note_ spamItem_ = new Note_();
            spamItem_.setStatus(2);
            noteArrayList.add(spamItem_);
            return noteArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.STICKY_NOTES);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Note_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.STICKY_NOTES).toString(), listType);
        } else {
            Note_ spamItem_ = new Note_();
            spamItem_.setStatus(11);
            noteArrayList.add(spamItem_);
            return noteArrayList;
        }
    }
}
