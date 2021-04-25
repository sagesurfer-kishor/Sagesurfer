package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 6/26/2018.
 */

public class State_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.IS_SELECTED)
    private boolean is_selected;

    @SerializedName(General.CITY)
    private ArrayList<City_> city;

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

    public ArrayList<City_> getCity() {
        return city;
    }

    public void setCity(ArrayList<City_> city) {
        this.city = city;
    }
}
