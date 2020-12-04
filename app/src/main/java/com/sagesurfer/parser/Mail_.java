package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.postcard.Postcard_;
import com.sagesurfer.models.PostcardAttachment_;
import com.sagesurfer.models.Responses_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 18-07-2017
 * Last Modified 26-09-2017
 */

public class Mail_ {

    public static ArrayList<Postcard_> parseMail(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Postcard_> mailArrayList = new ArrayList<>();
        Postcard_ postcard_ = new Postcard_();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                postcard_.setStatus(2);
                mailArrayList.add(postcard_);
                return mailArrayList;
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Postcard_>>() {
            }.getType();
            mailArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        }
        return mailArrayList;
    }

    public static Responses_ parseActions(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Responses_> responseArray;
        Responses_ responses_ = new Responses_();
        if (Error_.oauth(response, _context) != 0) {
            PostcardAttachment_ attachment_ = new PostcardAttachment_();
            attachment_.setStatus(13);
            return responses_;
        }
        if (response == null || response.trim().length() < 0) {
            PostcardAttachment_ attachment_ = new PostcardAttachment_();
            attachment_.setStatus(12);
            return responses_;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Responses_>>() {
        }.getType();
        responseArray = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        return responseArray.get(0);
    }
}
