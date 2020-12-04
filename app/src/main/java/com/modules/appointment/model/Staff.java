package com.modules.appointment.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Kailash Karankal on 2/6/2020.
 */
public class Staff implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.EMAIL)
    private String email;

    @SerializedName(General.GROUP_ID)
    private int group_id;

    @SerializedName(General.GROUP_NAME)
    private String group_name;

    @SerializedName(General.PHONE)
    private String phone = "";

    @SerializedName(General.STATUS)
    private int status;

    private boolean selected = false;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

