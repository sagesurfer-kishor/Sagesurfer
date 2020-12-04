package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 11/13/2018.
 */

public class MoodStats_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.ACTIVITY)
    private String activity;

    @SerializedName(General.URL)
    private String url;

    @SerializedName(General.COUNT)
    private int count;

    @SerializedName(General.DAY)
    private int day;

    @SerializedName(General.MONTH)
    private String month;

    @SerializedName(General.IS_ADDED)
    private int is_added;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.STATUS)
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getIs_added() {
        return is_added;
    }

    public void setIs_added(int is_added) {
        this.is_added = is_added;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
