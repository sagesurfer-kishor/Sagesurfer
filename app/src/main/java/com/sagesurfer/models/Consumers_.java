package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 8/2/2018.
 */

public class Consumers_ implements Serializable {
    @SerializedName(General.USER_ID)
    private long user_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.USERNAME) //Assignment
    private String username;

    @SerializedName(General.PEERPARTICIPANT_COUNT) //Assignment
    private String peerparticipant_count;

    @SerializedName(General.ID) //Consumer Report Assignment Graph
    private long id;

    @SerializedName(General.QOL) //Consumer Report Assignment Graph
    private int qol;

    @SerializedName(General.STATUS)
    private int status;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
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

    public String getPeerparticipant_count() {
        return peerparticipant_count;
    }

    public void setPeerparticipant_count(String peerparticipant_count) {
        this.peerparticipant_count = peerparticipant_count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQol() {
        return qol;
    }

    public void setQol(int qol) {
        this.qol = qol;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
