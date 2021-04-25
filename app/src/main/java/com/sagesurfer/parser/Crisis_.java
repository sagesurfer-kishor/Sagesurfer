package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.crisis.CrisisCount;
import com.modules.crisis.Risk_;
import com.modules.crisis.SupportPerson_;
import com.sagesurfer.constant.Actions_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 07-09-2017
 * Last Modified 26-09-2017
 */

public class Crisis_ {

    public static ArrayList<CrisisCount> parseCounts(String response, String TAG, Context _context) {
        ArrayList<CrisisCount> crisisCountArrayList = new ArrayList<>();
        if (response == null) {
            CrisisCount crisis_ = new CrisisCount();
            crisis_.setStatus(12);
            crisisCountArrayList.add(crisis_);
            return crisisCountArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            CrisisCount crisis_ = new CrisisCount();
            crisis_.setStatus(13);
            crisisCountArrayList.add(crisis_);
            return crisisCountArrayList;
        }
        if (Error_.noData(response, Actions_.CRISIS_COUNT, _context) == 2) {
            CrisisCount crisis_ = new CrisisCount();
            crisis_.setStatus(2);
            crisisCountArrayList.add(crisis_);
            return crisisCountArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.CRISIS_COUNT);
        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<CrisisCount>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.CRISIS_COUNT).toString(), listType);
        }
        return crisisCountArrayList;
    }

    public static ArrayList<Risk_> parseRisk(String response, String action, String TAG, Context _context) {
        ArrayList<Risk_> riskArrayList = new ArrayList<>();
        if (response == null) {
            Risk_ crisis_ = new Risk_();
            crisis_.setStatus(12);
            riskArrayList.add(crisis_);
            return riskArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Risk_ crisis_ = new Risk_();
            crisis_.setStatus(13);
            riskArrayList.add(crisis_);
            return riskArrayList;
        }
        if (Error_.noData(response, action, _context) == 2) {
            Risk_ crisis_ = new Risk_();
            crisis_.setStatus(2);
            riskArrayList.add(crisis_);
            return riskArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, action);
        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Risk_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, action).toString(), listType);
        }
        return riskArrayList;
    }

    public static ArrayList<SupportPerson_> parseSupportPerson(String response, String TAG, Context _context) {
        ArrayList<SupportPerson_> supportPersonArrayList = new ArrayList<>();
        if (response == null) {
            SupportPerson_ supportPerson_ = new SupportPerson_();
            supportPerson_.setStatus(12);
            supportPersonArrayList.add(supportPerson_);
            return supportPersonArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            SupportPerson_ supportPerson_ = new SupportPerson_();
            supportPerson_.setStatus(13);
            supportPersonArrayList.add(supportPerson_);
            return supportPersonArrayList;
        }
        if (Error_.noData(response, Actions_.SUPPORT_PERSONS, _context) == 2) {
            SupportPerson_ supportPerson_ = new SupportPerson_();
            supportPerson_.setStatus(2);
            supportPersonArrayList.add(supportPerson_);
            return supportPersonArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.SUPPORT_PERSONS);
        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SupportPerson_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.SUPPORT_PERSONS).toString(), listType);
        }
        return supportPersonArrayList;
    }
}
