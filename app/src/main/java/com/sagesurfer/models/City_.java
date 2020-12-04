package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 6/26/2018.
 */

public class City_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.IS_SELECTED)
    private boolean is_selected;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isSelected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}
