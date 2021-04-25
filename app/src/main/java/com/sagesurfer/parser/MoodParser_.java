package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.mood.MoodModel;
import com.sagesurfer.models.MoodJournalDataMood_;
import com.sagesurfer.models.MoodJournal_;
import com.sagesurfer.models.MoodStats_;
import com.sagesurfer.models.Responses_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 11/14/2018.
 */

public class MoodParser_ {
    public static ArrayList<MoodJournal_> parseJournal(String response, String jsonName, Context _context, String TAG) {
        ArrayList<MoodJournal_> journalArrayList = new ArrayList<>();
        try {
            if (response == null) {
                MoodJournal_ journalItem_ = new MoodJournal_();
                journalItem_.getData().get(0).getMood().get(0).setStatus(12);
                journalArrayList.add(journalItem_);
                return journalArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                MoodJournal_ journalItem_ = new MoodJournal_();
                journalItem_.getData().get(0).getMood().get(0).setStatus(13);
                journalArrayList.add(journalItem_);
                return journalArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                MoodJournal_ journalItem_ = new MoodJournal_();
                journalItem_.getData().get(0).getMood().get(0).setStatus(2);
                journalArrayList.add(journalItem_);
                return journalArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MoodJournal_>>() {
                }.getType();
                journalArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                MoodJournal_ journalItem_ = new MoodJournal_();
                journalItem_.getData().get(0).getMood().get(0).setStatus(11);
                journalArrayList.add(journalItem_);
                return journalArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return journalArrayList;
    }

    public static ArrayList<MoodJournalDataMood_> parseCalendar(String response, String jsonName, Context _context, String TAG) {
        ArrayList<MoodJournalDataMood_> calendarArrayList = new ArrayList<>();
        try {
            if (response == null) {
                MoodJournalDataMood_ calendarItem_ = new MoodJournalDataMood_();
                calendarItem_.setStatus(12);
                calendarArrayList.add(calendarItem_);
                return calendarArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                MoodJournalDataMood_ calendarItem_ = new MoodJournalDataMood_();
                calendarItem_.setStatus(13);
                calendarArrayList.add(calendarItem_);
                return calendarArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                MoodJournalDataMood_ calendarItem_ = new MoodJournalDataMood_();
                calendarItem_.setStatus(2);
                calendarArrayList.add(calendarItem_);
                return calendarArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MoodJournalDataMood_>>() {
                }.getType();
                calendarArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                MoodJournalDataMood_ calendarItem_ = new MoodJournalDataMood_();
                calendarItem_.setStatus(11);
                calendarArrayList.add(calendarItem_);
                return calendarArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return calendarArrayList;
    }

    public static ArrayList<MoodStats_> parseStatsCount(String response, String jsonName, Context _context, String TAG) {
        ArrayList<MoodStats_> statsCountArrayList = new ArrayList<>();
        try {
            if (response == null) {
                MoodStats_ statsCountItem_ = new MoodStats_();
                statsCountItem_.setStatus(12);
                statsCountArrayList.add(statsCountItem_);
                return statsCountArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                MoodStats_ statsCountItem_ = new MoodStats_();
                statsCountItem_.setStatus(13);
                statsCountArrayList.add(statsCountItem_);
                return statsCountArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                MoodStats_ statsCountItem_ = new MoodStats_();
                statsCountItem_.setStatus(2);
                statsCountArrayList.add(statsCountItem_);
                return statsCountArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MoodStats_>>() {
                }.getType();
                statsCountArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                MoodStats_ statsCountItem_ = new MoodStats_();
                statsCountItem_.setStatus(11);
                statsCountArrayList.add(statsCountItem_);
                return statsCountArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return statsCountArrayList;
    }

    public static ArrayList<Responses_> parseReminderStatus(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Responses_> reminderStatusArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Responses_ reminderStatusItem_ = new Responses_();
                reminderStatusItem_.setStatus(12);
                reminderStatusArrayList.add(reminderStatusItem_);
                return reminderStatusArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Responses_ reminderStatusItem_ = new Responses_();
                reminderStatusItem_.setStatus(13);
                reminderStatusArrayList.add(reminderStatusItem_);
                return reminderStatusArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Responses_ reminderStatusItem_ = new Responses_();
                reminderStatusItem_.setStatus(2);
                reminderStatusArrayList.add(reminderStatusItem_);
                return reminderStatusArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Responses_>>() {
                }.getType();
                reminderStatusArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Responses_ reminderStatusItem_ = new Responses_();
                reminderStatusItem_.setStatus(11);
                reminderStatusArrayList.add(reminderStatusItem_);
                return reminderStatusArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return reminderStatusArrayList;
    }

    public static ArrayList<MoodModel> parseMoodList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<MoodModel> moodList = new ArrayList<>();
        if (response == null) {
            MoodModel moodModel = new MoodModel();
            moodModel.setStatus(11);
            moodList.add(moodModel);
            return moodList;
        }
        if (Error_.oauth(response, _context) == 13) {
            MoodModel moodModel = new MoodModel();
            moodModel.setStatus(13);
            moodList.add(moodModel);
            return moodList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            MoodModel moodModel = new MoodModel();
            moodModel.setStatus(2);
            moodList.add(moodModel);
            return moodList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MoodModel>>() {
            }.getType();
            moodList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return moodList;
    }
}
