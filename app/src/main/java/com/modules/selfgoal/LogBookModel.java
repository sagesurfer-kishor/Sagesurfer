package com.modules.selfgoal;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class LogBookModel implements Serializable {

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.START_DATE)
    private String startDate;

    @SerializedName(General.END_DATE)
    private String endDate;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.YES)
    private String yes;

    @SerializedName(General.NO)
    private String no;

    @SerializedName(General.GOAL_STATUS)
    private String goalStatus;

    @SerializedName(General.GOAL_STATUS_ID)
    private String goalStatusID;

    @SerializedName(General.COMPLETED)
    private int completed;

    @SerializedName(General.PARTIALCOMPLETED)
    private int partialCompleted;

    @SerializedName(General.MISSED)
    private int missed;

    @SerializedName(General.INPUTNEEDED)
    private int inputneeded;

    @SerializedName(General.STATUS)
    private int status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

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

    public String getGoalStatus() {
        return goalStatus;
    }

    public void setGoalStatus(String goalStatus) {
        this.goalStatus = goalStatus;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getPartialCompleted() {
        return partialCompleted;
    }

    public void setPartialCompleted(int partialCompleted) {
        this.partialCompleted = partialCompleted;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public int getInputneeded() {
        return inputneeded;
    }

    public void setInputneeded(int inputneeded) {
        this.inputneeded = inputneeded;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGoalStatusID() {
        return goalStatusID;
    }

    public void setGoalStatusID(String goalStatusID) {
        this.goalStatusID = goalStatusID;
    }
}
