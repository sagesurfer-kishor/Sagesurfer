package com.sagesurfer.secure;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.models.GetAddNewMember;
import com.sagesurfer.models.GetGroupMembers;
import com.sagesurfer.models.GetGroupsCometchat;
import com.sagesurfer.models.Members_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupTeam_ {

    public static ArrayList<GetGroupMembers> parseGroupMembers(String response, String jsonName, Context _context, String TAG) {
        ArrayList<GetGroupMembers> teamsArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                return teamsArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<GetGroupMembers>>() {
            }.getType();
            teamsArrayList = gson.fromJson(Objects.requireNonNull(GetJson_.getArray(response, jsonName)).toString(), listType);
        }
        return teamsArrayList;
    }


    public static ArrayList<GetGroupsCometchat> parseTeams(String response, String jsonName, Context _context, String TAG) {
        ArrayList<GetGroupsCometchat> teamsArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                return teamsArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<GetGroupsCometchat>>() {
            }.getType();
            teamsArrayList = gson.fromJson(Objects.requireNonNull(GetJson_.getArray(response, jsonName)).toString(), listType);
        }
        return teamsArrayList;
    }

    public static ArrayList<GetAddNewMember> parsegroupMember(String response, String jsonName, Context _context, String TAG) {
        ArrayList<GetAddNewMember> teamsArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                return teamsArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<GetAddNewMember>>() {
            }.getType();
            teamsArrayList = gson.fromJson(Objects.requireNonNull(GetJson_.getArray(response, jsonName)).toString(), listType);
        }
        return teamsArrayList;
    }


    public static ArrayList<Members_> parseTeamsMember(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Members_> teamsArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, jsonName, _context) == 2) {
                return teamsArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<GetGroupsCometchat>>() {
            }.getType();
            teamsArrayList = gson.fromJson(Objects.requireNonNull(GetJson_.getArray(response, jsonName)).toString(), listType);
        }
        return teamsArrayList;
    }

}
