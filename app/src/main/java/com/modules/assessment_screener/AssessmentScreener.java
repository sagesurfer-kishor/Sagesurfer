package com.modules.assessment_screener;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class AssessmentScreener implements Serializable {

    @SerializedName(General.RECORD_ID)
    private int record_id;

    @SerializedName(General.FORM_ID)
    private int form_id;

    @SerializedName(General.FORM_NAME)
    private String form_name;

    @SerializedName(General.SENDER)
    private String sender;

    @SerializedName(General.COUNT)
    private int count;

    @SerializedName(General.ADDED_DATE)
    private long added_date;

    @SerializedName(General.STATUS)
    private int status;

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public int getForm_id() {
        return form_id;
    }

    public void setForm_id(int form_id) {
        this.form_id = form_id;
    }

    public String getForm_name() {
        return form_name;
    }

    public void setForm_name(String form_name) {
        this.form_name = form_name;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getAdded_date() {
        return added_date;
    }

    public void setAdded_date(long added_date) {
        this.added_date = added_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
