package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 7/16/2018.
 */

public class Members_ implements Serializable {
    @SerializedName(General.USER_ID)
    private long user_id;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.FULLNAME)
    private String fullname;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName(General.TEAM_NAME)
    private String team_name;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @SerializedName(General.TEAM_ID)
    private int team_id;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("comet_chat_id")
    private String comet_chat_id;

    public String getComet_chat_id() {
        return comet_chat_id;
    }

    public void setComet_chat_id(String comet_chat_id) {
        this.comet_chat_id = comet_chat_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
