package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.CSOverview_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 6/5/2018.
 */
//CaseloadSummaryOverviewParser_
public class CSOverviewParser_ {
    public static ArrayList<CSOverview_> parseCSOverview(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CSOverview_> csOverviewArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CSOverview_ caseloadItem_ = new CSOverview_();
                caseloadItem_.setStatus(12);
                csOverviewArrayList.add(caseloadItem_);
                return csOverviewArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CSOverview_ caseloadItem_ = new CSOverview_();
                caseloadItem_.setStatus(13);
                csOverviewArrayList.add(caseloadItem_);
                return csOverviewArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CSOverview_ caseloadItem_ = new CSOverview_();
                caseloadItem_.setStatus(2);
                csOverviewArrayList.add(caseloadItem_);
                return csOverviewArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CSOverview_>>() {
                }.getType();
                csOverviewArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CSOverview_ caseloadItem_ = new CSOverview_();
                caseloadItem_.setStatus(11);
                csOverviewArrayList.add(caseloadItem_);
                return csOverviewArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return csOverviewArrayList;
    }
}
