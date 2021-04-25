package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.modules.beahivoural_survey.model.BehaviouralHealth;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BehaviouralServey_ {

    public static ArrayList<BehaviouralHealth> parseBehaviouralList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<BehaviouralHealth> behaviouralHealthArrayList = new ArrayList<>();
        try {
            if (response == null) {
                BehaviouralHealth behaviouralHealth = new BehaviouralHealth();
                behaviouralHealth.setStatus(12);
                behaviouralHealthArrayList.add(behaviouralHealth);
                return behaviouralHealthArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                BehaviouralHealth behaviouralHealth = new BehaviouralHealth();
                behaviouralHealth.setStatus(13);
                behaviouralHealthArrayList.add(behaviouralHealth);
                return behaviouralHealthArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                BehaviouralHealth behaviouralHealth = new BehaviouralHealth();
                behaviouralHealth.setStatus(2);
                behaviouralHealthArrayList.add(behaviouralHealth);
                return behaviouralHealthArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<BehaviouralHealth>>() {
                }.getType();
                behaviouralHealthArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                BehaviouralHealth behaviouralHealth = new BehaviouralHealth();
                behaviouralHealth.setStatus(11);
                behaviouralHealthArrayList.add(behaviouralHealth);
                return behaviouralHealthArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return behaviouralHealthArrayList;
    }

    public static ArrayList<BehaviouralHealth> parseBehaviouralListOne(String response, String jsonName, Context _context, String TAG) {
        ArrayList<BehaviouralHealth> behaviouralHealthArrayList = new ArrayList<>();
        try {
            if (response == null) {
                BehaviouralHealth caseloadPeerNoteItem_ = new BehaviouralHealth();
                caseloadPeerNoteItem_.setStatus(12);
                behaviouralHealthArrayList.add(caseloadPeerNoteItem_);
                return behaviouralHealthArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                BehaviouralHealth caseloadPeerNoteItem_ = new BehaviouralHealth();
                caseloadPeerNoteItem_.setStatus(13);
                behaviouralHealthArrayList.add(caseloadPeerNoteItem_);
                return behaviouralHealthArrayList;
            }

            JsonParser parser = new JsonParser();
            JsonObject jObject = parser.parse(response).getAsJsonObject();
            JsonObject JsonObjectAllNotes = jObject.getAsJsonObject(jsonName);
            JsonArray jsonArray = JsonObjectAllNotes.getAsJsonArray("quesion_array");

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<BehaviouralHealth>>() {
                }.getType();
                behaviouralHealthArrayList = gson.fromJson(jsonArray, listType);
            } else {
                BehaviouralHealth caseloadPeerNoteItem_ = new BehaviouralHealth();
                caseloadPeerNoteItem_.setStatus(11);
                behaviouralHealthArrayList.add(caseloadPeerNoteItem_);
                return behaviouralHealthArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return behaviouralHealthArrayList;
    }
}
