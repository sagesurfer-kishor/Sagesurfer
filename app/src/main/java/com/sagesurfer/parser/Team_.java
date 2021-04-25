package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.TeamCounters_;
import com.sagesurfer.models.Teams_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team_ {

    public static ArrayList<Teams_> parseTeams(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Teams_> teamsArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                return teamsArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Teams_>>() {
            }.getType();
            teamsArrayList = gson.fromJson(Objects.requireNonNull(GetJson_.getArray(response, jsonName)).toString(), listType);
        }
        return teamsArrayList;
    }

    public static ArrayList<TeamCounters_> parseTeamCounter(String response, String jsonName, Context _context, String TAG) {
        ArrayList<TeamCounters_> teamCountersArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                return teamCountersArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<TeamCounters_>>() {
            }.getType();
            teamCountersArrayList = gson.fromJson(Objects.requireNonNull(GetJson_.getArray(response, jsonName)).toString(), listType);
        }
        return teamCountersArrayList;
    }
}
