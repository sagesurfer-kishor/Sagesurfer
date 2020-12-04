package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * Created by Monika on 8/21/2018.
 */

public class PORDocumentData_ {
    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.LABLE)
    private String lable;

    @SerializedName(General.CNT)
    private int cnt;

    @SerializedName(General.ACTIVITY_ID)
    private int activity_id;

    @SerializedName("key")
    private String key;

    @SerializedName(General.VALUE)
    private String value;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
