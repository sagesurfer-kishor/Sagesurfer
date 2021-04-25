package com.modules.selfgoal;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class LogBookDotsModel implements Serializable {

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.COLOR)
    private String color;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
