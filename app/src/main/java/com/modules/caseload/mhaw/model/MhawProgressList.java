package com.modules.caseload.mhaw.model;


import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class MhawProgressList implements Serializable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.ADDED_BY_ID)
    private int added_by_id;

    @SerializedName(General.ADDED_BY_NAME)
    private String added_by_name;

    @SerializedName(General.FINALIZE_NOTE)
    private int finalize_note;

    @SerializedName(General.GOAL_ADDRESS)
    private String goal_address;

    @SerializedName(General.OBJECT_ADDRESS)
    private String obj_address;

    @SerializedName(General.REASON)
    private String reason;

    @SerializedName(General.POSTED_DATE)
    private long posted_date;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.SERVICE)
    private String service;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.BARRIERS_PRESENTED)
    private String barriers_presented;

    @SerializedName(General.HOME_WORK)
    private String home_work;

    @SerializedName(General.MEETING_DATE)
    private String meeting_date;

    @SerializedName(General.MEETING_TIME)
    private String meeting_time;

    @SerializedName(General.PROGRESS_ADDRESS)
    private String progress_address;

    @SerializedName(General.SERVICE_ADDRESS)
    private String service_address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdded_by_id() {
        return added_by_id;
    }

    public void setAdded_by_id(int added_by_id) {
        this.added_by_id = added_by_id;
    }

    public String getAdded_by_name() {
        return added_by_name;
    }

    public void setAdded_by_name(String added_by_name) {
        this.added_by_name = added_by_name;
    }

    public int getFinalize_note() {
        return finalize_note;
    }

    public void setFinalize_note(int finalize_note) {
        this.finalize_note = finalize_note;
    }

    public String getGoal_address() {
        return goal_address;
    }

    public void setGoal_address(String goal_address) {
        this.goal_address = goal_address;
    }

    public String getObj_address() {
        return obj_address;
    }

    public void setObj_address(String obj_address) {
        this.obj_address = obj_address;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(long posted_date) {
        this.posted_date = posted_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBarriers_presented() {
        return barriers_presented;
    }

    public void setBarriers_presented(String barriers_presented) {
        this.barriers_presented = barriers_presented;
    }

    public String getHome_work() {
        return home_work;
    }

    public void setHome_work(String home_work) {
        this.home_work = home_work;
    }

    public String getMeeting_date() {
        return meeting_date;
    }

    public void setMeeting_date(String meeting_date) {
        this.meeting_date = meeting_date;
    }

    public String getMeeting_time() {
        return meeting_time;
    }

    public void setMeeting_time(String meeting_time) {
        this.meeting_time = meeting_time;
    }

    public String getProgress_address() {
        return progress_address;
    }

    public void setProgress_address(String progress_address) {
        this.progress_address = progress_address;
    }

    public String getService_address() {
        return service_address;
    }

    public void setService_address(String service_address) {
        this.service_address = service_address;
    }
}
