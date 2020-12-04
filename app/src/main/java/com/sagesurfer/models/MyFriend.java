package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class MyFriend implements Serializable {

    @SerializedName(General.USER_ID)
    private String userId;

    @SerializedName(General.COMET_CHAT_ID)
    private String comet_chat_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName("photo")
    private String photo;

    @SerializedName(General.STATUS)
    private Integer status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComet_chat_id() {
        return comet_chat_id;
    }

    public void setComet_chat_id(String comet_chat_id) {
        this.comet_chat_id = comet_chat_id;
    }
}
