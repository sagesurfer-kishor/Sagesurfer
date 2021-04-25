package com.modules.caseload.werhope.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class ProgressList implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.STUD_ID)
    private int stud_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.MOOD_ID)
    private int mood_id;

    @SerializedName(General.MOOD)
    private String mood;

    @SerializedName(General.MOOD_IMAGE)
    private String mood_image;

    @SerializedName(General.MOOD_COLOR)
    private String mood_color;

    @SerializedName(General.WEEKLY_THEME)
    private String weekly_theme;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.ATTENDANCE)
    private String attendance;

    @SerializedName(General.START_TIME)
    private String start_time;

    @SerializedName(General.END_TIME)
    private String end_time;

    @SerializedName(General.TOTAL_TIME)
    private String total_time;

    @SerializedName(General.NOTES)
    private String notes;

    @SerializedName(General.REASON)
    private String reason;

    @SerializedName(General.POSTED_DATE)
    private long posted_date;

    @SerializedName(General.ADDED_BY_ID)
    private int added_by_id;

    @SerializedName(General.ADDED_BY_NAME)
    private String added_by_name;

    @SerializedName(General.ADD_BY_ROLE_ID)
    private int added_by_role_id;

    @SerializedName(General.ADDED_BY_ROLE)
    private String added_by_role;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.UPDATE_COUNT)
    private int count;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStud_id() {
        return stud_id;
    }

    public void setStud_id(int stud_id) {
        this.stud_id = stud_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMood_id() {
        return mood_id;
    }

    public void setMood_id(int mood_id) {
        this.mood_id = mood_id;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getMood_image() {
        return mood_image;
    }

    public void setMood_image(String mood_image) {
        this.mood_image = mood_image;
    }

    public String getMood_color() {
        return mood_color;
    }

    public void setMood_color(String mood_color) {
        this.mood_color = mood_color;
    }

    public String getWeekly_theme() {
        return weekly_theme;
    }

    public void setWeekly_theme(String weekly_theme) {
        this.weekly_theme = weekly_theme;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public int getAdded_by_role_id() {
        return added_by_role_id;
    }

    public void setAdded_by_role_id(int added_by_role_id) {
        this.added_by_role_id = added_by_role_id;
    }

    public String getAdded_by_role() {
        return added_by_role;
    }

    public void setAdded_by_role(String added_by_role) {
        this.added_by_role = added_by_role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
