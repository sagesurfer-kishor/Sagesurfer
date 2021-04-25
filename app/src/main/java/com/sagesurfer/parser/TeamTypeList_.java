package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.reports.appointment_reports.model.ClientListModel;
import com.modules.team.TeamTypeModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TeamTypeList_ {
    public static ArrayList<TeamTypeModel> parseTeamTypeClientList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<TeamTypeModel> progressList = new ArrayList<>();
        if (response == null) {
            TeamTypeModel progress = new TeamTypeModel();
            progress.setStatus(11);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) == 13) {
            TeamTypeModel progress = new TeamTypeModel();
            progress.setStatus(13);
            progressList.add(progress);
            return progressList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            TeamTypeModel progress = new TeamTypeModel();
            progress.setStatus(2);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TeamTypeModel>>() {
            }.getType();
            progressList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return progressList;
    }

}
