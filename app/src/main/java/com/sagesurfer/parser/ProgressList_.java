package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.caseload.werhope.model.ProgressList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProgressList_ {
    public static ArrayList<ProgressList> parseProgressList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<ProgressList> progressList = new ArrayList<>();
        if (response == null) {
            ProgressList progress = new ProgressList();
            progress.setStatus(11);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) == 13) {
            ProgressList progress = new ProgressList();
            progress.setStatus(13);
            progressList.add(progress);
            return progressList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            ProgressList progress = new ProgressList();
            progress.setStatus(2);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ProgressList>>() {
            }.getType();
            progressList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return progressList;
    }

}
