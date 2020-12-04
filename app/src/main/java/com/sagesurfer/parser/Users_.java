package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.CometChatTeamMembers_;
import com.sagesurfer.models.Friends_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 31-07-2017
 * Last Modified 26-09-2017
 */

public class Users_ {

    public static ArrayList<Friends_> parse(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Friends_> friendsArrayList = new ArrayList<>();
        if (response == null) {
            return friendsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            return friendsArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            return friendsArrayList;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Friends_>>() {
        }.getType();
        friendsArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        return friendsArrayList;
    }

    public static ArrayList<Participant_> parseParticipants(String response, Context _context, String TAG) {
        ArrayList<Participant_> friendsArrayList;
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Participant_>>() {
        }.getType();
        friendsArrayList = gson.fromJson(response, listType);
        return friendsArrayList;
    }

    public static ArrayList<Friends_> parseTaskFriends(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Friends_> friendsArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                return friendsArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Friends_>>() {
            }.getType();
            friendsArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        }
        return friendsArrayList;
    }

    public static ArrayList<CometChatTeamMembers_> parseCometChatTeamMembers(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CometChatTeamMembers_> teamMembersArrayList = new ArrayList<>();
        if (response == null) {
            return teamMembersArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            return teamMembersArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            return teamMembersArrayList;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CometChatTeamMembers_>>() {
        }.getType();
        teamMembersArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        return teamMembersArrayList;
    }
}
