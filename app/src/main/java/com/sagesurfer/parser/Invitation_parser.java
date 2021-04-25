package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.admin.Invitation_;
import com.modules.team.team_invitation_werhope.model.Invitation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Invitation_parser {

    public static ArrayList<Invitations_> parseSupervisorListPopup(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Invitations_> invitationsArrayList = new ArrayList<>();
        if (response == null) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(11);
            invitationsArrayList.add(invitations_);
            return invitationsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(13);
            invitationsArrayList.add(invitations_);
            return invitationsArrayList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            Invitations_ invitations_ = new Invitations_();
            invitations_.setStatus(2);
            invitationsArrayList.add(invitations_);
            return invitationsArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Invitations_>>() {
            }.getType();
            invitationsArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return invitationsArrayList;
    }
}
