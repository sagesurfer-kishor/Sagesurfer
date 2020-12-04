package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.motivation.model.MotivationLibrary_;
import com.sagesurfer.models.MoodJournalDataMood_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 3/28/2019.
 */

public class MotivationParser_ {
    public static ArrayList<MotivationLibrary_> parseMotivation(String response, String jsonName, Context _context, String TAG) {
        ArrayList<MotivationLibrary_> motivationArrayList = new ArrayList<>();
        try {
            if (response == null) {
                MotivationLibrary_ motivationItem_ = new MotivationLibrary_();
                motivationItem_.setStatus(12);
                motivationArrayList.add(motivationItem_);
                return motivationArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                MotivationLibrary_ motivationItem_ = new MotivationLibrary_();
                motivationItem_.setStatus(13);
                motivationArrayList.add(motivationItem_);
                return motivationArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                MotivationLibrary_ motivationItem_ = new MotivationLibrary_();
                motivationItem_.setStatus(2);
                motivationArrayList.add(motivationItem_);
                return motivationArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MotivationLibrary_>>() {
                }.getType();
                motivationArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                MotivationLibrary_ motivationItem_ = new MotivationLibrary_();
                motivationItem_.setStatus(11);
                motivationArrayList.add(motivationItem_);
                return motivationArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return motivationArrayList;
    }

    public static ArrayList<MoodJournalDataMood_> parseMood(String response, String jsonName, Context _context, String TAG) {
        ArrayList<MoodJournalDataMood_> moodArrayList = new ArrayList<>();
        try {
            if (response == null) {
                MoodJournalDataMood_ moodItem_ = new MoodJournalDataMood_();
                moodItem_.setStatus(12);
                moodArrayList.add(moodItem_);
                return moodArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                MoodJournalDataMood_ moodItem_ = new MoodJournalDataMood_();
                moodItem_.setStatus(13);
                moodArrayList.add(moodItem_);
                return moodArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                MoodJournalDataMood_ moodItem_ = new MoodJournalDataMood_();
                moodItem_.setStatus(2);
                moodArrayList.add(moodItem_);
                return moodArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MoodJournalDataMood_>>() {
                }.getType();
                moodArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                MoodJournalDataMood_ moodItem_ = new MoodJournalDataMood_();
                moodItem_.setStatus(11);
                moodArrayList.add(moodItem_);
                return moodArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return moodArrayList;
    }
}
