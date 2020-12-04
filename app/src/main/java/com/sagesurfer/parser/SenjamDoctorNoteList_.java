package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.caseload.mhaw.model.MhawProgressList;
import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.onetime_dailysurvey.model.OneTimeDailySurvey;
import com.modules.sows.model.SowsNotes;
import com.sagesurfer.models.DailyDosingSelfGoal_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SenjamDoctorNoteList_ {
    public static ArrayList<SenjamListModel> parseDoctorSowsList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<SenjamListModel> progressList = new ArrayList<>();
        if (response == null) {
            SenjamListModel progress = new SenjamListModel();
            progress.setStatus(11);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) == 13) {
            SenjamListModel progress = new SenjamListModel();
            progress.setStatus(13);
            progressList.add(progress);
            return progressList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            SenjamListModel progress = new SenjamListModel();
            progress.setStatus(2);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SenjamListModel>>() {
            }.getType();
            progressList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return progressList;
    }

    public static ArrayList<SowsNotes> parseSowsNoteList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<SowsNotes> sowsNotesArrayList = new ArrayList<>();
        if (response == null) {
            SowsNotes sowsNotes = new SowsNotes();
            sowsNotes.setStatus(11);
            sowsNotesArrayList.add(sowsNotes);
            return sowsNotesArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            SowsNotes sowsNotes = new SowsNotes();
            sowsNotes.setStatus(13);
            sowsNotesArrayList.add(sowsNotes);
            return sowsNotesArrayList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            SowsNotes sowsNotes = new SowsNotes();
            sowsNotes.setStatus(2);
            sowsNotesArrayList.add(sowsNotes);
            return sowsNotesArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SowsNotes>>() {
            }.getType();
            sowsNotesArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return sowsNotesArrayList;
    }

    public static ArrayList<DailyDosingSelfGoal_> parseDailyDosingSelfGoal(String response, String jsonName, Context _context, String TAG) {
        ArrayList<DailyDosingSelfGoal_> sowsNotesArrayList = new ArrayList<>();
        if (response == null) {
            DailyDosingSelfGoal_ sowsNotes = new DailyDosingSelfGoal_();
            sowsNotes.setStatus(11);
            sowsNotesArrayList.add(sowsNotes);
            return sowsNotesArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            DailyDosingSelfGoal_ sowsNotes = new DailyDosingSelfGoal_();
            sowsNotes.setStatus(13);
            sowsNotesArrayList.add(sowsNotes);
            return sowsNotesArrayList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            DailyDosingSelfGoal_ sowsNotes = new DailyDosingSelfGoal_();
            sowsNotes.setStatus(2);
            sowsNotesArrayList.add(sowsNotes);
            return sowsNotesArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<DailyDosingSelfGoal_>>() {
            }.getType();
            sowsNotesArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return sowsNotesArrayList;
    }

}
