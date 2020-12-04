package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.goal_assignment.model.AssignedGoals;
import com.modules.selfgoal.LogBookModel;
import com.modules.selfgoal.werhope_self_goal.model.LogBook;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.Goal_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 15/03/2018
 * Last Modified on
 */
public class SelfGoal_ {

    public static ArrayList<Goal_> parseSpams(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Goal_> goalArrayList = new ArrayList<>();
        if (response == null) {
            Goal_ spamItem_ = new Goal_();
            spamItem_.setStatus(12);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Goal_ spamItem_ = new Goal_();
            spamItem_.setStatus(13);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            Goal_ spamItem_ = new Goal_();
            spamItem_.setStatus(2);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, jsonName);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Goal_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        } else {
            Goal_ spamItem_ = new Goal_();
            spamItem_.setStatus(11);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
    }

    public static ArrayList<LogBook> logBook(String response, String jsonName, Context _context, String TAG) {
        ArrayList<LogBook> goalArrayList = new ArrayList<>();
        if (response == null) {
            LogBook spamItem_ = new LogBook();
            spamItem_.setStatus(12);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            LogBook spamItem_ = new LogBook();
            spamItem_.setStatus(13);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            LogBook spamItem_ = new LogBook();
            spamItem_.setStatus(2);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, jsonName);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<LogBook>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        } else {
            LogBook spamItem_ = new LogBook();
            spamItem_.setStatus(11);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
    }

    public static ArrayList<LogBookModel> senjamLogBook(String response, String jsonName, Context _context, String TAG) {
        ArrayList<LogBookModel> goalArrayList = new ArrayList<>();
        if (response == null) {
            LogBookModel spamItem_ = new LogBookModel();
            spamItem_.setStatus(12);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            LogBookModel spamItem_ = new LogBookModel();
            spamItem_.setStatus(13);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            LogBookModel spamItem_ = new LogBookModel();
            spamItem_.setStatus(2);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, jsonName);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<LogBookModel>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        } else {
            LogBookModel spamItem_ = new LogBookModel();
            spamItem_.setStatus(11);
            goalArrayList.add(spamItem_);
            return goalArrayList;
        }
    }

    public static ArrayList<Goal_> parseSelfGoal(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Goal_> consumerArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Goal_ consumerItem_ = new Goal_();
                consumerItem_.setStatus(12);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Goal_ consumerItem_ = new Goal_();
                consumerItem_.setStatus(13);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Goal_ consumerItem_ = new Goal_();
                consumerItem_.setStatus(2);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Goal_>>() {
                }.getType();
                consumerArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Goal_ consumerItem_ = new Goal_();
                consumerItem_.setStatus(11);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return consumerArrayList;
    }

    public static ArrayList<Goal_> parseDefaultsSelfGoal(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Goal_> consumerArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Goal_ consumerItem_ = new Goal_();
                consumerItem_.setStatus(12);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Goal_ consumerItem_ = new Goal_();
                consumerItem_.setStatus(13);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Goal_ consumerItem_ = new Goal_();
                consumerItem_.setStatus(2);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Goal_>>() {
                }.getType();
                consumerArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Goal_ consumerItem_ = new Goal_();
                consumerItem_.setStatus(11);
                consumerArrayList.add(consumerItem_);
                return consumerArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return consumerArrayList;
    }

    public static ArrayList<Choices_> parseSchoolList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Choices_> schoolArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Choices_ school = new Choices_();
                school.setStatus(12);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Choices_ school = new Choices_();
                school.setStatus(13);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Choices_ school = new Choices_();
                school.setStatus(2);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Choices_>>() {
                }.getType();
                schoolArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Choices_ school = new Choices_();
                school.setStatus(11);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schoolArrayList;
    }

    public static ArrayList<AssignedGoals> parseGoalPopup(String response, String jsonName, Context _context, String TAG) {
        ArrayList<AssignedGoals> schoolArrayList = new ArrayList<>();
        try {
            if (response == null) {
                AssignedGoals school = new AssignedGoals();
                school.setStatus(12);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                AssignedGoals school = new AssignedGoals();
                school.setStatus(13);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                AssignedGoals school = new AssignedGoals();
                school.setStatus(2);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<AssignedGoals>>() {
                }.getType();
                schoolArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                AssignedGoals school = new AssignedGoals();
                school.setStatus(11);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schoolArrayList;
    }

}
