package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.admin.Invitation_;
import com.modules.admin.SpamItem_;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.models.Friends_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 22-08-2017
 * Last Modified 26-09-2017
 */

public class Admin_ {

    public static ArrayList<SpamItem_> parseSpams(String response, Context _context, String TAG) {
        ArrayList<SpamItem_> spamItemArrayList = new ArrayList<>();
        if (response == null) {
            SpamItem_ spamItem_ = new SpamItem_();
            spamItem_.setStatus(12);
            spamItemArrayList.add(spamItem_);
            return spamItemArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            SpamItem_ spamItem_ = new SpamItem_();
            spamItem_.setStatus(13);
            spamItemArrayList.add(spamItem_);
            return spamItemArrayList;
        }
        if (Error_.noData(response, Actions_.ADMIN_ACTIVITY, _context) == 2) {
            SpamItem_ spamItem_ = new SpamItem_();
            spamItem_.setStatus(2);
            spamItemArrayList.add(spamItem_);
            return spamItemArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.ADMIN_ACTIVITY);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SpamItem_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.ADMIN_ACTIVITY).toString(), listType);
        } else {
            SpamItem_ spamItem_ = new SpamItem_();
            spamItem_.setStatus(11);
            spamItemArrayList.add(spamItem_);
            return spamItemArrayList;
        }
    }

    public static ArrayList<Invitation_> parseInvitations(String response, Context _context, String TAG) {
        ArrayList<Invitation_> invitationArrayList = new ArrayList<>();
        if (response == null) {
            Invitation_ invitation_ = new Invitation_();
            invitation_.setStatus(12);
            invitationArrayList.add(invitation_);
            return invitationArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Invitation_ invitation_ = new Invitation_();
            invitation_.setStatus(13);
            invitationArrayList.add(invitation_);
            return invitationArrayList;
        }
        if (Error_.noData(response, Actions_.GET_INVITATIONS, _context) == 2) {
            Invitation_ invitation_ = new Invitation_();
            invitation_.setStatus(2);
            invitationArrayList.add(invitation_);
            return invitationArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.GET_INVITATIONS);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Invitation_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.GET_INVITATIONS).toString(), listType);
        } else {
            Invitation_ invitation_ = new Invitation_();
            invitation_.setStatus(11);
            invitationArrayList.add(invitation_);
            return invitationArrayList;
        }
    }

    public static ArrayList<Friends_> parseUsers(String response, Context _context, String TAG) {
        ArrayList<Friends_> usersList = new ArrayList<>();
        if (response == null) {
            Friends_ friends_ = new Friends_();
            friends_.setStatus(12);
            usersList.add(friends_);
            return usersList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Friends_ friends_ = new Friends_();
            friends_.setStatus(13);
            usersList.add(friends_);
            return usersList;
        }
        if (Error_.noData(response, "user_details", _context) == 2) {
            Friends_ friends_ = new Friends_();
            friends_.setStatus(2);
            usersList.add(friends_);
            return usersList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, "user_details");

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Friends_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, "user_details").toString(), listType);
        } else {
            Friends_ friends_ = new Friends_();
            friends_.setStatus(11);
            usersList.add(friends_);
            return usersList;
        }
    }
}
