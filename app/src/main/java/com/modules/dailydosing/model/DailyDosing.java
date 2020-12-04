package com.modules.dailydosing.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class DailyDosing implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.YES)
    private String yes;

    @SerializedName(General.NO)
    private String no;

    @SerializedName(General.GOAL_STATUS)
    private String goal_status;

    @SerializedName(General.GOAL_STATUS_ID)
    private String goal_status_id;

    @SerializedName(General.STATUS)
    private int status;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYes() {
        return yes;
    }

    public void setYes(String yes) {
        this.yes = yes;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getGoal_status() {
        return goal_status;
    }

    public void setGoal_status(String goal_status) {
        this.goal_status = goal_status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGoal_status_id() {
        return goal_status_id;
    }

    public void setGoal_status_id(String goal_status_id) {
        this.goal_status_id = goal_status_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
