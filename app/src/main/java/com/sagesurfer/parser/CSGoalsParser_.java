package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.CSGoals_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 6/6/2018.
 */
//CaseloadSummaryGoalsParser_
public class CSGoalsParser_ {
    public static ArrayList<CSGoals_> parseCSProfile(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CSGoals_> csGoalsArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CSGoals_ caseloadItem_ = new CSGoals_();
                caseloadItem_.setStatus(12);
                csGoalsArrayList.add(caseloadItem_);
                return csGoalsArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CSGoals_ caseloadItem_ = new CSGoals_();
                caseloadItem_.setStatus(13);
                csGoalsArrayList.add(caseloadItem_);
                return csGoalsArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CSGoals_ caseloadItem_ = new CSGoals_();
                caseloadItem_.setStatus(2);
                csGoalsArrayList.add(caseloadItem_);
                return csGoalsArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CSGoals_>>() {
                }.getType();
                csGoalsArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CSGoals_ caseloadItem_ = new CSGoals_();
                caseloadItem_.setStatus(11);
                csGoalsArrayList.add(caseloadItem_);
                return csGoalsArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return csGoalsArrayList;
    }
}

