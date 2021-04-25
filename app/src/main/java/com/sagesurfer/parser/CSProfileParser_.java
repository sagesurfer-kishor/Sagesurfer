package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.CSProfile_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 6/5/2018.
 */
//CaseloadSummaryProfileParser_
public class CSProfileParser_ {
    public static ArrayList<CSProfile_> parseCSProfile(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CSProfile_> csProfileArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CSProfile_ caseloadItem_ = new CSProfile_();
                caseloadItem_.setStatus(12);
                csProfileArrayList.add(caseloadItem_);
                return csProfileArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CSProfile_ caseloadItem_ = new CSProfile_();
                caseloadItem_.setStatus(13);
                csProfileArrayList.add(caseloadItem_);
                return csProfileArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CSProfile_ caseloadItem_ = new CSProfile_();
                caseloadItem_.setStatus(2);
                csProfileArrayList.add(caseloadItem_);
                return csProfileArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CSProfile_>>() {
                }.getType();
                csProfileArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CSProfile_ caseloadItem_ = new CSProfile_();
                caseloadItem_.setStatus(11);
                csProfileArrayList.add(caseloadItem_);
                return csProfileArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return csProfileArrayList;
    }
}
