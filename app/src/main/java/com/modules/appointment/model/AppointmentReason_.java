package com.modules.appointment.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Kailash Karankal on 2/6/2020.
 */
public class AppointmentReason_ implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.REASON)
    private String reason;

    private boolean selected;

    @SerializedName(General.STATUS)
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

