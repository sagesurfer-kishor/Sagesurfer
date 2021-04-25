package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.Server_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class Servers_ {

    public static ArrayList<Server_> parse(String response, Context _context, String TAG) {
        ArrayList<Server_> serverArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, "instances", _context) == 2) {
                return serverArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Server_>>() {
            }.getType();
            serverArrayList = gson.fromJson(GetJson_.getArray(response, "instances").toString(), listType);
        }
        return serverArrayList;
    }
}
