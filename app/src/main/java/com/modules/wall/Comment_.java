package com.modules.wall;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 13-07-2017
 *         Last Modified on 15-12-2017
 */
public class Comment_ {

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.PROFILE_PHOTO)
    private String profile_photo;

    @SerializedName("timestamp")
    private long date;

    @SerializedName("comment_text")
    private String comment;

    @SerializedName(General.STATUS)
    private int status;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePhoto(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    public String getProfilePhoto() {
        return this.profile_photo;
    }

    public long getDate() {
        return this.date;
    }

    public String getComment(){
        return this.comment;
    }
}
