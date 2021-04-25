package com.modules.caseload.mhaw.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class MhawAmendmentList implements Serializable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.ADDED_BY_ID)
    private int added_by_id;

    @SerializedName(General.ADDED_BY_NAME)
    private String added_by_name;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.POSTED_DATE)
    private long posted_date;

    @SerializedName(General.STATUS)
    private int status;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
