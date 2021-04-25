package com.modules.crisis;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 07-09-2017
 *         Last Modified on 16-11-2017
 */

public class CrisisCount {

    @SerializedName("latest_crisis")
    private String latest_crisis;

    @SerializedName("crisis_date")
    private String crisis_date;

    @SerializedName("crisis_time")
    private String crisis_time;

    @SerializedName("total_count")
    private String total_count;

    @SerializedName("active_count")
    private String active_count;

    @SerializedName("successful_count")
    private String successful_count;

    @SerializedName("most_frequent_used")
    private String most_frequent_used;

    @SerializedName(General.TIMESTAMP)
    private long timestamp;

    @SerializedName(General.STATUS)
    private int status;

    public void setLatest_crisis(String latest_crisis) {
        this.latest_crisis = latest_crisis;
    }

    public void setCrisis_date(String crisis_date) {
        this.crisis_date = crisis_date;
    }

    public void setCrisis_time(String crisis_time) {
        this.crisis_time = crisis_time;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public void setActive_count(String active_count) {
        this.active_count = active_count;
    }

    public void setSuccessful_count(String successful_count) {
        this.successful_count = successful_count;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setMost_frequent_used(String most_frequent_used) {
        this.most_frequent_used = most_frequent_used;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Method */

    public String getLatest_crisis() {
        return latest_crisis;
    }

    public String getCrisis_date() {
        return crisis_date;
    }

    public String getCrisis_time() {
        return crisis_time;
    }

    public String getTotal_count() {
        return total_count;
    }

    public String getActive_count() {
        return active_count;
    }

    public String getSuccessful_count() {
        return successful_count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMost_frequent_used() {
        if (most_frequent_used.trim().length() <= 0) {
            return "-- NA --";
        }
        return most_frequent_used;
    }

    public int getStatus() {
        return status;
    }
}
