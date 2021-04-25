package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.models.DailyPlanner_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.models.Task_;

import java.util.ArrayList;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 16-08-2017
 * Last Modified 26-09-2017
 */

public class Planner_ {

    public static ArrayList<DailyPlanner_> parseFeed(String response, Context _context, String TAG) {
        Gson gson = new Gson();
        ArrayList<DailyPlanner_> dailyPlannerArrayList = new ArrayList<>();

        if (response == null) {
            DailyPlanner_ dailyPlanner_ = new DailyPlanner_();
            dailyPlanner_.setStatus(12);
            dailyPlannerArrayList.add(dailyPlanner_);
            return dailyPlannerArrayList;
        }

        if (Error_.oauth(response, _context) == 13) {
            DailyPlanner_ dailyPlanner_ = new DailyPlanner_();
            dailyPlanner_.setStatus(13);
            dailyPlannerArrayList.add(dailyPlanner_);
            return dailyPlannerArrayList;
        }

        if (Error_.noData(response, Actions_.GET_DATA, _context) == 2) {
            DailyPlanner_ dailyPlanner_ = new DailyPlanner_();
            dailyPlanner_.setStatus(2);
            dailyPlannerArrayList.add(dailyPlanner_);
            return dailyPlannerArrayList;
        }

        JsonArray jsonArray = GetJson_.getArray(response, Actions_.GET_DATA);
        if (jsonArray == null) {
            DailyPlanner_ dailyPlanner_ = new DailyPlanner_();
            dailyPlanner_.setStatus(11);
            dailyPlannerArrayList.add(dailyPlanner_);
            return dailyPlannerArrayList;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            if (jsonObject.has(General.STATUS)) {
                int status = jsonObject.get(General.STATUS).getAsInt();
                if (status == 1) {
                    DailyPlanner_ dailyPlanner_ = new DailyPlanner_();

                    int type = jsonObject.get(General.TYPE).getAsInt();
                    //announcement
                    if (type == 1) {
                        Announcement_ announcement_ = gson.fromJson(jsonObject, Announcement_.class);
                        dailyPlanner_.setAnnouncement(announcement_);
                        dailyPlanner_.setStatus(1);
                        dailyPlanner_.setType(type);
                        dailyPlannerArrayList.add(dailyPlanner_);
                    }
                    //task list
                    if (type == 2 || type == 3) {
                        Task_ task_ = gson.fromJson(jsonObject, Task_.class);
                        dailyPlanner_.setTask(task_);
                        dailyPlanner_.setStatus(1);
                        dailyPlanner_.setType(type);
                        dailyPlannerArrayList.add(dailyPlanner_);
                    }
                    //event
                    if (type == 4) {
                        Event_ event_ = gson.fromJson(jsonObject, Event_.class);
                        dailyPlanner_.setEvent(event_);
                        dailyPlanner_.setStatus(1);
                        dailyPlanner_.setType(type);
                        dailyPlannerArrayList.add(dailyPlanner_);
                    }
                    //goal
                    if (type == 5) {
                        Goal_ goal_ = gson.fromJson(jsonObject, Goal_.class);
                        dailyPlanner_.setGoal(goal_);
                        dailyPlanner_.setStatus(1);
                        dailyPlanner_.setType(type);
                        dailyPlannerArrayList.add(dailyPlanner_);
                    }
                } else {
                    DailyPlanner_ dailyPlanner_ = new DailyPlanner_();
                    dailyPlanner_.setStatus(status);
                    dailyPlannerArrayList.add(dailyPlanner_);
                    return dailyPlannerArrayList;
                }
            } else {
                DailyPlanner_ dailyPlanner_ = new DailyPlanner_();
                dailyPlanner_.setStatus(11);
                dailyPlannerArrayList.add(dailyPlanner_);
                return dailyPlannerArrayList;
            }
        }
        return dailyPlannerArrayList;
    }
}
