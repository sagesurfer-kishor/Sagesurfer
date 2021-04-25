package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Choices_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kailash Karankal on 1/6/2020.
 */
public class FriendInvitation_ {

    public static ArrayList<Choices_> friendInvitationList(String response, Context _context, String TAG) {
        ArrayList<Choices_> choicesArrayList = new ArrayList<>();
        if (response == null) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(11);
            choicesArrayList.add(choices_);
            return choicesArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(13);
            choicesArrayList.add(choices_);
            return choicesArrayList;
        }

        if (Error_.noData(response, General.PENDING_INVITATION, _context) == 2) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(2);
            choicesArrayList.add(choices_);
            return choicesArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Choices_>>() {
            }.getType();
            choicesArrayList = gson.fromJson(GetJson_.getArray(response, General.PENDING_INVITATION)
                    .toString(), listType);
        }
        return choicesArrayList;
    }
}
