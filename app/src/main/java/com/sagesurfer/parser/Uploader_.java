package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.PostcardAttachment_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 20-07-2017
 * Last Modified 26-09-2017
 */
public class Uploader_ {

    public static ArrayList<PostcardAttachment_> parse(String response, String jsonName, Context _context, String TAG) {
        ArrayList<PostcardAttachment_> uploaderList = new ArrayList<>();
        if (Error_.oauth(response, _context) != 0) {
            PostcardAttachment_ attachment_ = new PostcardAttachment_();
            attachment_.setStatus(13);
            uploaderList.add(attachment_);
            return uploaderList;
        }
        if (response == null || response.trim().length() < 0) {
            PostcardAttachment_ attachment_ = new PostcardAttachment_();
            attachment_.setStatus(12);
            uploaderList.add(attachment_);
            return uploaderList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            PostcardAttachment_ attachment_ = new PostcardAttachment_();
            attachment_.setStatus(2);
            uploaderList.add(attachment_);
            return uploaderList;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PostcardAttachment_>>() {
        }.getType();
        uploaderList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);

        return uploaderList;
    }
}
