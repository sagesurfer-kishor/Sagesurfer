package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.modules.wall.Comment_;
import com.modules.wall.Feed_;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.storage.database.operations.DatabaseInsert_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 10-07-2017
 *         Last Modified on 10-07-2017
 */

public class Wall_ {

    public static ArrayList<Feed_> parseFeed(String response, Context _context, String TAG) {
        ArrayList<Feed_> feedArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            JsonObject jsonObject = GetJson_.getJson(response);
            String utc = jsonObject.get("current_timestamp").getAsString();

            DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(_context);
            databaseInsert_.insertUpdateLog(General.FEED, utc);

            if (Error_.noData(response, Actions_.WALL_FEEDS, _context) == 2) {
                return feedArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Feed_>>() {
            }.getType();
            feedArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.WALL_FEEDS).toString(), listType);
        }
        return feedArrayList;
    }

    public static ArrayList<Comment_> parseComment(String response, Context _context, String TAG) {
        ArrayList<Comment_> commentArrayList = new ArrayList<>();
        if (response != null && Error_.oauth(response, _context) == 0) {
            if (Error_.noData(response, General.COMMENT, _context) == 2) {
                return commentArrayList;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Comment_>>() {
            }.getType();
            commentArrayList = gson.fromJson(GetJson_.getArray(response, General.COMMENT).toString(), listType);
        }
        return commentArrayList;
    }
}
