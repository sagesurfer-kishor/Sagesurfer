package com.modules.team;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class TeamTypeModel implements Serializable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.NAME)
    private String name;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @SerializedName(General.STATUS)
    private int status;


}
