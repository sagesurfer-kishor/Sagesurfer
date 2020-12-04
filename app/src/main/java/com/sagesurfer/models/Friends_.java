package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 31-07-2017
 *         Last Modified on 31-07-2017
 */

public class Friends_ implements Serializable {

    @SerializedName(General.USER_ID)
    private int user_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName(General.ROLE)
    private String role;

    @SerializedName("user_access")
    private int user_access;

    @SerializedName(General.STATUS)
    private int status;

    private boolean isSelected;

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUser_access(int user_access) {
        this.user_access = user_access;
    }

    /*** Getter Methods ***/

    public int getStatus() {
        return status;
    }

    public int getUserId() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoto() {
        return photo;
    }

    public String getRole() {
        return role;
    }

    public boolean getSelected() {
        return this.isSelected;
    }

    public int getUser_access() {
        return user_access;
    }
}

