package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 11/14/2018.
 */

public class MoodJournalDataMood_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.MOOD_URL)
    private String mood_url;

    @SerializedName(General.MOOD_NAME)
    private String mood_name;

    @SerializedName(General.ACTIVITY_URL)
    private String activity_url;

    @SerializedName(General.ACTIVITY_NAME)
    private String activity_name;

    @SerializedName(General.MOOD_NOTE)
    private String mood_note;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.TIME)
    private String time;

    @SerializedName(General.LOCATION)
    private String location;

    @SerializedName(General.INTENSITY)
    private int intensity;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.SET_BY)
    private String set_by;

    @SerializedName(General.MOOD_ID) //motivation
    private long mood_id;

    @SerializedName(General.ADDED_DATE) //motivation
    private long added_date;

    @SerializedName(General.MOOD) //motivation
    private String mood;

    @SerializedName(General.SET_BY_ROLL_NAME)
    private String set_by_roll_name;

    @SerializedName(General.OTHER_ACTIVITY_NAME)
    private String other_activity_name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMood_url() {
        return mood_url;
    }

    public void setMood_url(String mood_url) {
        this.mood_url = mood_url;
    }

    public String getMood_name() {
        return mood_name;
    }

    public void setMood_name(String mood_name) {
        this.mood_name = mood_name;
    }

    public String getActivity_url() {
        return activity_url;
    }

    public void setActivity_url(String activity_url) {
        this.activity_url = activity_url;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getMood_note() {
        return mood_note;
    }

    public void setMood_note(String mood_note) {
        this.mood_note = mood_note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSet_by() {
        return set_by;
    }

    public void setSet_by(String set_by) {
        this.set_by = set_by;
    }

    public String getSet_by_roll_name() {
        return set_by_roll_name;
    }

    public void setSet_by_roll_name(String set_by_roll_name) {
        this.set_by_roll_name = set_by_roll_name;
    }

    public String getOther_activity_name() {
        return other_activity_name;
    }

    public void setOther_activity_name(String other_activity_name) {
        this.other_activity_name = other_activity_name;
    }

    public long getMood_id() {
        return mood_id;
    }

    public void setMood_id(long mood_id) {
        this.mood_id = mood_id;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public long getAdded_date() {
        return added_date;
    }

    public void setAdded_date(long added_date) {
        this.added_date = added_date;
    }
}
