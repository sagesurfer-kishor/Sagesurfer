package com.modules.team.team_invitation_werhope.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class Invitation implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.USER_ID)
    private int user_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.EMAIL)
    private String email;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.ROLE)
    private String role;

    @SerializedName(General.REQUEST_STATUS)
    private String request_status;

    @SerializedName(General.TIMESTAMP)
    private long timestamp;

    @SerializedName(General.STATUS)
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
