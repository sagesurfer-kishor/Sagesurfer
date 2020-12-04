package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.Consumers_;
import com.sagesurfer.models.PORActivityData_;
import com.sagesurfer.models.PORDocumentData_;
import com.sagesurfer.models.PURData;
import com.sagesurfer.models.PURDeviceData_;
import com.sagesurfer.models.PURDeviceStatistics_;
import com.sagesurfer.models.Teams_;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 8/2/2018.
 */

public class ReportsParser_ {

    public static ArrayList<Consumers_> parseConsumers(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Consumers_> consumerArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Consumers_ consumerItem_ = new Consumers_();
                consumerItem_.setStatus(12);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Consumers_ consumerItem_ = new Consumers_();
                consumerItem_.setStatus(13);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Consumers_ consumerItem_ = new Consumers_();
                consumerItem_.setStatus(2);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Consumers_>>() {
                }.getType();
                consumerArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Consumers_ consumerItem_ = new Consumers_();
                consumerItem_.setStatus(11);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return consumerArrayList;
    }

    public static ArrayList<PURDeviceStatistics_> parseDeviceStatistics(String response, String jsonName, Context _context, String TAG) {
        ArrayList<PURDeviceStatistics_> deviceStatisticsArrayList = new ArrayList<>();
        try {
            if (response == null) {
                PURDeviceStatistics_ deviceStatisticsItem_ = new PURDeviceStatistics_();
                deviceStatisticsItem_.setStatus(12);
                deviceStatisticsArrayList.add(deviceStatisticsItem_);
                return deviceStatisticsArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                PURDeviceStatistics_ deviceStatisticsItem_ = new PURDeviceStatistics_();
                deviceStatisticsItem_.setStatus(13);
                deviceStatisticsArrayList.add(deviceStatisticsItem_);
                return deviceStatisticsArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                PURDeviceStatistics_ deviceStatisticsItem_ = new PURDeviceStatistics_();
                deviceStatisticsItem_.setStatus(2);
                deviceStatisticsArrayList.add(deviceStatisticsItem_);
                return deviceStatisticsArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<PURDeviceStatistics_>>() {
                }.getType();
                deviceStatisticsArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                PURDeviceStatistics_ deviceStatisticsItem_ = new PURDeviceStatistics_();
                deviceStatisticsItem_.setStatus(11);
                deviceStatisticsArrayList.add(deviceStatisticsItem_);
                return deviceStatisticsArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return deviceStatisticsArrayList;
    }

    public static ArrayList<PURDeviceData_> parseDeviceData(String response, String jsonName, Context _context, String TAG) {
        ArrayList<PURDeviceData_> deviceDataArrayList = new ArrayList<>();
        try {
            if (response == null) {
                PURDeviceData_ deviceDataItem_ = new PURDeviceData_();
                deviceDataItem_.setStatus(12);
                deviceDataArrayList.add(deviceDataItem_);
                return deviceDataArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                PURDeviceData_ deviceDataItem_ = new PURDeviceData_();
                deviceDataItem_.setStatus(13);
                deviceDataArrayList.add(deviceDataItem_);
                return deviceDataArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                PURDeviceData_ deviceDataItem_ = new PURDeviceData_();
                deviceDataItem_.setStatus(2);
                deviceDataArrayList.add(deviceDataItem_);
                return deviceDataArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<PURDeviceData_>>() {
                }.getType();
                deviceDataArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                PURDeviceData_ deviceDataItem_ = new PURDeviceData_();
                deviceDataItem_.setStatus(11);
                deviceDataArrayList.add(deviceDataItem_);
                return deviceDataArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return deviceDataArrayList;
    }

    public static ArrayList<PORActivityData_> parseActivityData(String response, String jsonName, Context _context, String TAG) {
        ArrayList<PORActivityData_> activityDataArrayList = new ArrayList<>();
        try {
            if (response == null) {
                PORActivityData_ activityDataItem_ = new PORActivityData_();
                activityDataItem_.setStatus(12);
                activityDataArrayList.add(activityDataItem_);
                return activityDataArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                PORActivityData_ activityDataItem_ = new PORActivityData_();
                activityDataItem_.setStatus(13);
                activityDataArrayList.add(activityDataItem_);
                return activityDataArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                PORActivityData_ activityDataItem_ = new PORActivityData_();
                activityDataItem_.setStatus(2);
                activityDataArrayList.add(activityDataItem_);
                return activityDataArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<PORActivityData_>>() {
                }.getType();
                activityDataArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                PORActivityData_ activityDataItem_ = new PORActivityData_();
                activityDataItem_.setStatus(11);
                activityDataArrayList.add(activityDataItem_);
                return activityDataArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return activityDataArrayList;
    }

    public static ArrayList<PORDocumentData_> parseDocumentData(String response, String jsonName, Context _context, String TAG) {
        ArrayList<PORDocumentData_> documentDataArrayList = new ArrayList<>();
        try {
            if (response == null) {
                PORDocumentData_ documentDataItem_ = new PORDocumentData_();
                documentDataItem_.setStatus(12);
                documentDataArrayList.add(documentDataItem_);
                return documentDataArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                PORDocumentData_ documentDataItem_ = new PORDocumentData_();
                documentDataItem_.setStatus(13);
                documentDataArrayList.add(documentDataItem_);
                return documentDataArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                PORDocumentData_ documentDataItem_ = new PORDocumentData_();
                documentDataItem_.setStatus(2);
                documentDataArrayList.add(documentDataItem_);
                return documentDataArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<PORDocumentData_>>() {
                }.getType();
                documentDataArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                PORDocumentData_ documentDataItem_ = new PORDocumentData_();
                documentDataItem_.setStatus(11);
                documentDataArrayList.add(documentDataItem_);
                return documentDataArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return documentDataArrayList;
    }

    public static ArrayList<Teams_> parseTeamList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Teams_> teamArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Teams_ teamItem_ = new Teams_();
                teamItem_.setStatus(12);
                teamArrayList.add(teamItem_);
                return teamArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Teams_ teamItem_ = new Teams_();
                teamItem_.setStatus(13);
                teamArrayList.add(teamItem_);
                return teamArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Teams_ teamItem_ = new Teams_();
                teamItem_.setStatus(2);
                teamArrayList.add(teamItem_);
                return teamArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Teams_>>() {
                }.getType();
                teamArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Teams_ teamItem_ = new Teams_();
                teamItem_.setStatus(11);
                teamArrayList.add(teamItem_);
                return teamArrayList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return teamArrayList;
    }
}
