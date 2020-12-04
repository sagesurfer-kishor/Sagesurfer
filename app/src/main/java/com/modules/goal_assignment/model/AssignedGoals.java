package com.modules.goal_assignment.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Kailash Karankal on 12/26/2019.
 */
public class AssignedGoals implements Serializable {
    @SerializedName("wer_goal_id")
    private long wer_goal_id;

    @SerializedName("main_goal_id")
    private long main_goal_id;

    @SerializedName("goal_name")
    private String goal_name;

    @SerializedName("goal_question")
    private String goal_question;

    @SerializedName(General.START_DATE)
    private String start_date;

    @SerializedName(General.END_DATE)
    private String end_date;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.PROGRESS)
    private int progress;

    @SerializedName("goal_current_status")
    private int goal_current_status;

    @SerializedName("isAddedAnyInputs")
    private int isAddedAnyInputs;

    @SerializedName("date_diff")
    private String date_diff;

    @SerializedName(General.ERROR)
    private String error;

    private boolean selected = false;

    private String yes;

    private String no;

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

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public long getWer_goal_id() {
        return wer_goal_id;
    }

    public void setWer_goal_id(long wer_goal_id) {
        this.wer_goal_id = wer_goal_id;
    }

    public long getMain_goal_id() {
        return main_goal_id;
    }

    public void setMain_goal_id(long main_goal_id) {
        this.main_goal_id = main_goal_id;
    }

    public String getGoal_name() {
        return goal_name;
    }

    public void setGoal_name(String goal_name) {
        this.goal_name = goal_name;
    }

    public String getGoal_question() {
        return goal_question;
    }

    public void setGoal_question(String goal_question) {
        this.goal_question = goal_question;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getGoal_current_status() {
        return goal_current_status;
    }

    public void setGoal_current_status(int goal_current_status) {
        this.goal_current_status = goal_current_status;
    }

    public int getIsAddedAnyInputs() {
        return isAddedAnyInputs;
    }

    public void setIsAddedAnyInputs(int isAddedAnyInputs) {
        this.isAddedAnyInputs = isAddedAnyInputs;
    }

    public String getDate_diff() {
        return date_diff;
    }

    public void setDate_diff(String date_diff) {
        this.date_diff = date_diff;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
