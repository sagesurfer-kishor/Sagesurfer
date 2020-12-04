package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class DailyDosingSelfGoal_  implements Serializable {

    @SerializedName(General.GOAL_ID)
    private String goal_id;

    @SerializedName(General.MAIN_GOAL_ID)
    private String main_goal_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.QUESTION)
    private String question;

    @SerializedName(General.ADDED_DATE)
    private int added_date;

    @SerializedName(General.STATUS)
    private int status;

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public String getMain_goal_id() {
        return main_goal_id;
    }

    public void setMain_goal_id(String main_goal_id) {
        this.main_goal_id = main_goal_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAdded_date() {
        return added_date;
    }

    public void setAdded_date(int added_date) {
        this.added_date = added_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
