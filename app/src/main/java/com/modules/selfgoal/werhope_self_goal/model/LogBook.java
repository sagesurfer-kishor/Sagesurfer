package com.modules.selfgoal.werhope_self_goal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kailash Karankal on 1/10/2020.
 */
public class LogBook implements Serializable {

    @SerializedName("goal_status")
    private String goal_status;

    @SerializedName("date_time")
    private String date_time;

    @SerializedName("input")
    private String input;

    @SerializedName("status")
    private int status;

    @SerializedName("error")
    private String error;

    public String getGoal_status() {
        return goal_status;
    }

    public void setGoal_status(String goal_status) {
        this.goal_status = goal_status;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

