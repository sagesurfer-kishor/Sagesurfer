package com.modules.leave_management.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Kailash Karankal on 12/19/2019.
 */
public class Leave implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName("from_date")
    private String from_date;

    @SerializedName(General.TO_DATE)
    private String to_date;

    @SerializedName(General.REASON)
    private String reason;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
