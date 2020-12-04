package com.modules.leave_management.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 12/18/2019.
 */
public class LeaveManagement implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName("from_date")
    private String from_date;

    @SerializedName(General.TO_DATE)
    private String to_date;

    @SerializedName(General.REASON)
    private String reason;

    @SerializedName(General.COLOR)
    private String color;

    @SerializedName(General.AVAILABLE)
    private String available;

    @SerializedName(General.LAST_UPDATED)
    private Long last_updated;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("leave")
    private ArrayList<Leave> leaveArrayList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Long last_updated) {
        this.last_updated = last_updated;
    }

    public int getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public ArrayList<Leave> getLeaveArrayList() {
        return leaveArrayList;
    }

    public void setLeaveArrayList(ArrayList<Leave> leaveArrayList) {
        this.leaveArrayList = leaveArrayList;
    }
}

