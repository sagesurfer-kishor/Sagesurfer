package com.sagesurfer.parser;

import android.content.Context;

import com.cometchat.pro.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.models.MyFriend;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FriendList {
    public static ArrayList<MyFriend> parseSpams(String response, Context _context, String TAG)
    {
        ArrayList<MyFriend> UserArrayList = new ArrayList<>();
        if (response == null) {
            MyFriend user = new MyFriend();
            user.setStatus(12);
            UserArrayList.add(user);
            return UserArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            MyFriend user = new MyFriend();
            user.setStatus(13);
            UserArrayList.add(user);
            return UserArrayList;
        }
        if (Error_.noData(response, Actions_.FRIENDS, _context) == 2) {
            MyFriend user = new MyFriend();
            user.setStatus(2);
            UserArrayList.add(user);
            return UserArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.FRIENDS);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MyFriend>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.FRIENDS).toString(), listType);
        } else {
            MyFriend user = new MyFriend();
           // user.setStatus(11);
            UserArrayList.add(user);
            return UserArrayList;
        }
    }

}
