package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.caseload.mhaw.model.MhawAmendmentList;
import com.modules.caseload.mhaw.model.MhawProgressList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MhawAmendmentList_ {
    public static ArrayList<MhawAmendmentList> parseProgressList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<MhawAmendmentList> progressList = new ArrayList<>();
        if (response == null) {
            MhawAmendmentList progress = new MhawAmendmentList();
            progress.setStatus(11);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) == 13) {
            MhawAmendmentList progress = new MhawAmendmentList();
            progress.setStatus(13);
            progressList.add(progress);
            return progressList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            MhawAmendmentList progress = new MhawAmendmentList();
            progress.setStatus(2);
            progressList.add(progress);
            return progressList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MhawAmendmentList>>() {
            }.getType();
            progressList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return progressList;
    }

}
