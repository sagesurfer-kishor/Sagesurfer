package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.caseload.mhaw.model.MhawProgressList;
import com.modules.reports.appointment_reports.model.ClientListModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MhawSelectClientList_ {
    public static ArrayList<ClientListModel> parseSelectClientList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<ClientListModel> progressList = new ArrayList<>();
        if (response == null) {
            ClientListModel progress = new ClientListModel();
            progress.setStatus(11);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) == 13) {
            ClientListModel progress = new ClientListModel();
            progress.setStatus(13);
            progressList.add(progress);
            return progressList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            ClientListModel progress = new ClientListModel();
            progress.setStatus(2);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ClientListModel>>() {
            }.getType();
            progressList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return progressList;
    }

}
