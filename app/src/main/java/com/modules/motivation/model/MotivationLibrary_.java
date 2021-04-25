package com.modules.motivation.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.MoodJournalDataMood_;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Monika on 3/28/2019.
 */

public class MotivationLibrary_ implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.USER_ID)
    private long user_id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.CONTENT_TYPE)
    private String content_type;

    @SerializedName(General.CONTENT_PATH)
    private String content_path;

    @SerializedName(General.LOG_ID)
    private long log_id;

    @SerializedName(General.SIZE)
    private String size;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.MOOD)
    private ArrayList<MoodJournalDataMood_> mood;

    @SerializedName(General.MOOD_LOCATION)
    private String mood_location;

    @SerializedName(General.STATUS)
    private int status;

    private boolean selected = false;


    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent_path() {
        return content_path;
    }

    public void setContent_path(String content_path) {
        this.content_path = content_path;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public ArrayList<MoodJournalDataMood_> getMood() {
        return mood;
    }

    public void setMood(ArrayList<MoodJournalDataMood_> mood) {
        this.mood = mood;
    }

    public String getMood_location() {
        return mood_location;
    }

    public void setMood_location(String mood_location) {
        this.mood_location = mood_location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
