package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.beahivoural_survey.model.BehaviouralHealth;
import com.modules.journaling.model.Journal_;
import com.modules.postcard.Postcard_;
import com.modules.re_assignment.model.ReAssignment;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kailash Karankal on 10/9/2019.
 */
public class Journaling_ {
    public static ArrayList<Journal_> parseJournaling(String response, Context _context, String TAG) {
        ArrayList<Journal_> journalArrayList = new ArrayList<>();
        if (response == null) {
            Journal_ journal_ = new Journal_();
            journal_.setStatus(11);
            journalArrayList.add(journal_);
            return journalArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Journal_ journal_ = new Journal_();
            journal_.setStatus(13);
            journalArrayList.add(journal_);
            return journalArrayList;
        }

        if (Error_.noData(response, Actions_.GET_LIST, _context) == 2) {
            Journal_ journal_ = new Journal_();
            journal_.setStatus(2);
            journalArrayList.add(journal_);
            return journalArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Journal_>>() {
            }.getType();
            journalArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_LIST)
                    .toString(), listType);
        }
        return journalArrayList;
    }

    public static ArrayList<Postcard_> parseMessageDetails(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Postcard_> postcardArrayList = new ArrayList<>();
        if (response == null) {
            Postcard_ postcard_ = new Postcard_();
            postcard_.setStatus(11);
            postcardArrayList.add(postcard_);
            return postcardArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Postcard_ postcard_ = new Postcard_();
            postcard_.setStatus(13);
            postcardArrayList.add(postcard_);
            return postcardArrayList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            Postcard_ postcard_ = new Postcard_();
            postcard_.setStatus(2);
            postcardArrayList.add(postcard_);
            return postcardArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Postcard_>>() {
            }.getType();
            postcardArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return postcardArrayList;
    }

    public static ArrayList<BehaviouralHealth> parseBHSDetails(String response, String jsonName, Context _context, String TAG) {
        ArrayList<BehaviouralHealth> behaviouralHealthArrayList = new ArrayList<>();
        if (response == null) {
            BehaviouralHealth behaviouralHealth = new BehaviouralHealth();
            behaviouralHealth.setStatus(11);
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
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<BehaviouralHealth>>() {
            }.getType();
            behaviouralHealthArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return behaviouralHealthArrayList;
    }


    public static ArrayList<Journal_> parseJournalingDetails(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Journal_> journalArrayList = new ArrayList<>();
        if (response == null) {
            Journal_ journal_ = new Journal_();
            journal_.setStatus(11);
            journalArrayList.add(journal_);
            return journalArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Journal_ journal_ = new Journal_();
            journal_.setStatus(13);
            journalArrayList.add(journal_);
            return journalArrayList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            Journal_ journal_ = new Journal_();
            journal_.setStatus(2);
            journalArrayList.add(journal_);
            return journalArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Journal_>>() {
            }.getType();
            journalArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return journalArrayList;
    }

    public static ArrayList<ReAssignment> parseReAssignment(String response, Context _context, String TAG) {
        ArrayList<ReAssignment> reAssignmentArrayList = new ArrayList<>();
        if (response == null) {
            ReAssignment reAssignment = new ReAssignment();
            reAssignment.setStatus(11);
            reAssignmentArrayList.add(reAssignment);
            return reAssignmentArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            ReAssignment reAssignment = new ReAssignment();
            reAssignment.setStatus(13);
            reAssignmentArrayList.add(reAssignment);
            return reAssignmentArrayList;
        }

        if (Error_.noData(response, General.GET_REASSIGNMENT_DETAILS, _context) == 2) {
            ReAssignment reAssignment = new ReAssignment();
            reAssignment.setStatus(2);
            reAssignmentArrayList.add(reAssignment);
            return reAssignmentArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ReAssignment>>() {
            }.getType();
            reAssignmentArrayList = gson.fromJson(GetJson_.getArray(response, General.GET_REASSIGNMENT_DETAILS)
                    .toString(), listType);
        }
        return reAssignmentArrayList;
    }
}
