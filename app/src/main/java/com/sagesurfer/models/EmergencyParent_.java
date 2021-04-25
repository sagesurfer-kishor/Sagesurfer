package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class EmergencyParent_ implements Serializable {

    @SerializedName(General.TEAM)
    private String team;

    @SerializedName("youth")
    private String youth;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("owner_image")
    private String owner_image;

    @SerializedName(General.ROLE)
    private String role;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName("home")
    private String home;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName(General.STATUS)
    private int status;

    public void setName(String name) {
        this.name = name;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setYouth(String youth) {
        this.youth = youth;
    }

    public void setOwner_image(String owner_image) {
        this.owner_image = owner_image;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTeam() {
        return team;
    }

    public String getYouth() {
        return youth;
    }

    public String getName() {
        return name;
    }

    public String getOwner_image() {
        return owner_image;
    }

    public String getRole() {
        return role;
    }

    public String getImage() {
        return image;
    }

    public String getHome() {
        return home;
    }

    public String getMobile() {
        return mobile;
    }

    public int getStatus() {
        return status;
    }
}
