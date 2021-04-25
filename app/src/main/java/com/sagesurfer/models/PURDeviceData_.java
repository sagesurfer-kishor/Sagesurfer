package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Monika on 8/6/2018.
 */

//PlatformUsageReportDeviceData_
public class PURDeviceData_ implements Serializable {
    @SerializedName(General.ACT_KEY)
    private String act_key;

    @SerializedName(General.ACT_NAME)
    private String act_name;

    @SerializedName(General.PERCENTAGE)
    private long percentage;

    @SerializedName(General.DEVICE)
    private String device;

    @SerializedName(General.VALUE)
    private int value;

    @SerializedName(General.TOTAL)
    private int total;

    @SerializedName(General.STATUS)
    private int status;

    public String getAct_key() {
        return act_key;
    }

    public void setAct_key(String act_key) {
        this.act_key = act_key;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public long getPercentage() {
        return percentage;
    }

    public void setPercentage(long percentage) {
        this.percentage = percentage;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
