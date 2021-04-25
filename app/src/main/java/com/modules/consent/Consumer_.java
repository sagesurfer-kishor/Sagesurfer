package com.modules.consent;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 09-08-2017
 *         Last Modified on 16-11-2017
 */


public class Consumer_ {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.USER_ID)
    private long user_id;

    @SerializedName(General.USERNAME)
    private String user_name;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(long id) {
        this.id = id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Methods */

    public long getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public int getStatus() {
        return status;
    }
}
