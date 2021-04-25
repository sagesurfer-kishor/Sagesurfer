package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.supervisor.Message_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 28-07-2017
 * Last Modified 26-09-2017
 */
public class Supervisor_ {

    public static ArrayList<Message_> parseMessage(String response, Context _context, String TAG) {
        ArrayList<Message_> messageArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, "message_list", _context) == 2) {
                Message_ message_ = new Message_();
                message_.setStatus(2);
                messageArrayList.add(message_);
                return messageArrayList;
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Message_>>() {
            }.getType();
            messageArrayList = gson.fromJson(GetJson_.getArray(response, "message_list").toString(), listType);
        }
        return messageArrayList;
    }

    public static ArrayList<Message_> parseMessageList(String response, Context _context, String TAG) {
        ArrayList<Message_> messageArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, "message_details", _context) == 2) {
                return messageArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Message_>>() {
            }.getType();
            messageArrayList = gson.fromJson(GetJson_.getArray(response, "message_details").toString(), listType);
        }
        return messageArrayList;
    }
    //
}
