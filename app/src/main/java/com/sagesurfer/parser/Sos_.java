package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.sos.MySos_;
import com.modules.sos.ReceivedSos_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 17-07-2017
 * Last Modified 26-09-2017
 */

public class Sos_ {

    public static ArrayList<MySos_> parseMySos(String response, Context _context, String TAG) {
        ArrayList<MySos_> sosArrayList = new ArrayList<>();
        if (response == null) {
            MySos_ mySos_ = new MySos_();
            mySos_.setStatus(12);
            sosArrayList.add(mySos_);
            return sosArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            MySos_ mySos_ = new MySos_();
            mySos_.setStatus(12);
            sosArrayList.add(mySos_);
            return sosArrayList;
        }
        if (Error_.noData(response, "my_sos", _context) == 2) {
            MySos_ mySos_ = new MySos_();
            mySos_.setStatus(2);
            sosArrayList.add(mySos_);
            return sosArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, "my_sos");
        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MySos_>>() {
            }.getType();
            sosArrayList = gson.fromJson(GetJson_.getArray(response, "my_sos").toString(), listType);
            return sosArrayList;
        } else {
            MySos_ mySos_ = new MySos_();
            mySos_.setStatus(11);
            sosArrayList.add(mySos_);
            return sosArrayList;
        }
    }

    public static ArrayList<ReceivedSos_> parseReceivedSos(String response, Context _context, String TAG) {
        ArrayList<ReceivedSos_> sosArrayList = new ArrayList<>();
        if (response == null) {
            ReceivedSos_ receivedSos_ = new ReceivedSos_();
            receivedSos_.setStatus(12);
            sosArrayList.add(receivedSos_);
            return sosArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            ReceivedSos_ receivedSos_ = new ReceivedSos_();
            receivedSos_.setStatus(13);
            sosArrayList.add(receivedSos_);
            return sosArrayList;
        }
        if (Error_.noData(response, "sos", _context) == 2) {
            ReceivedSos_ receivedSos_ = new ReceivedSos_();
            receivedSos_.setStatus(2);
            sosArrayList.add(receivedSos_);
            return sosArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, "sos");
        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ReceivedSos_>>() {
            }.getType();
            sosArrayList = gson.fromJson(GetJson_.getArray(response, "sos").toString(), listType);
            return sosArrayList;
        } else {
            ReceivedSos_ receivedSos_ = new ReceivedSos_();
            receivedSos_.setStatus(11);
            sosArrayList.add(receivedSos_);
            return sosArrayList;
        }
    }
}
