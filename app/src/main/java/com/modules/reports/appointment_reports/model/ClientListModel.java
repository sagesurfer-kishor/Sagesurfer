package com.modules.reports.appointment_reports.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class ClientListModel implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.TOTAL)
    private String total;

    @SerializedName(General.NO_SHOWS)
    private String no_shows;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getNo_shows() {
        return no_shows;
    }

    public void setNo_shows(String no_shows) {
        this.no_shows = no_shows;
    }

    public String getSatisfaction_avg() {
        return satisfaction_avg;
    }

    public void setSatisfaction_avg(String satisfaction_avg) {
        this.satisfaction_avg = satisfaction_avg;
    }

    @SerializedName(General.SATISFACTION_AVG)
    private String satisfaction_avg;

    @SerializedName(General.STATUS)
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
