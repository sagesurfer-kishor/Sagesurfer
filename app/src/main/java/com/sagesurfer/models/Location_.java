package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * Created by Monika on 7/2/2018.
 */

public class Location_ {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.MSG)
    private String msg;

    @SerializedName(General.STATUS)
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
