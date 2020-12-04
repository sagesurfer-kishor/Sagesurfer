package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.dailydosing.model.DailyDosing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SenjamDailyDosingList_ {
    public static ArrayList<DailyDosing> parseDailyDosingList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<DailyDosing> dailyDosingsList = new ArrayList<>();
        if (response == null) {
            DailyDosing progress = new DailyDosing();
//            progress.setStatus(11);
            dailyDosingsList.add(progress);
            return dailyDosingsList;
        }
        if (Error_.oauth(response, _context) == 13) {
            DailyDosing progress = new DailyDosing();
//            progress.setStatus(13);
            dailyDosingsList.add(progress);
            return dailyDosingsList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            DailyDosing progress = new DailyDosing();
            progress.setStatus(2);
            dailyDosingsList.add(progress);
            return dailyDosingsList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<DailyDosing>>() {
            }.getType();
            dailyDosingsList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return dailyDosingsList;
    }

    public static ArrayList<DailyDosing> parseDailyDosingPatientList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<DailyDosing> dailyDosingsList = new ArrayList<>();
        if (response == null) {
            DailyDosing progress = new DailyDosing();
//            progress.setStatus(11);
            dailyDosingsList.add(progress);
            return dailyDosingsList;
        }
        if (Error_.oauth(response, _context) == 13) {
            DailyDosing progress = new DailyDosing();
//            progress.setStatus(13);
            dailyDosingsList.add(progress);
            return dailyDosingsList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            DailyDosing progress = new DailyDosing();
            progress.setStatus(2);
            dailyDosingsList.add(progress);
            return dailyDosingsList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<DailyDosing>>() {
            }.getType();
            dailyDosingsList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return dailyDosingsList;
    }

}
