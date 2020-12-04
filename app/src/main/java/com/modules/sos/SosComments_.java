package com.modules.sos;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 28-07-2017
 *         Last Modified on 14-12-2017
 **/


class SosComments_ {

    @SerializedName(General.MSG)
    private String msg;

    @SerializedName(General.USER_ID)
    private int user_id;

    @SerializedName("current_status")
    private int current_status;

    @SerializedName(General.TIMESTAMP)
    private long timestamp;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName("profile_pic")
    private String profile_pic;

    @SerializedName(General.STATUS)
    private int status;

    /*** Setter Methods ***/

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setCurrentStatus(int current_status) {
        this.current_status = current_status;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setName(String full_name) {
        this.full_name = full_name;
    }

    public void setPhoto(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*** Getter Methods ***/

    public String getMessage() {
        return this.msg;
    }

    public int getUserId() {
        return this.user_id;
    }

    public int getCurrentStatus() {
        return this.current_status;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getName() {
        return this.full_name;
    }

    public String getPhoto() {
        return this.profile_pic;
    }

    public int getStatus() {
        return this.status;
    }
}
