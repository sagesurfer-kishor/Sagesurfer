package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/11/2018
 *         Last Modified on 4/11/2018
 */

public class Comments_ {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("profile_pic")
    private String profile_pic;

    @SerializedName(General.COMMENT)
    private String comment;

    @SerializedName("comment_date")
    private long comment_date;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    @SerializedName(General.STATUS)
    private int status;

    /* Setter Methods */

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(long comment_date) {
        this.comment_date = comment_date;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Methods */

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getComment() {
        return comment;
    }

    public long getDate() {
        return comment_date;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public int getStatus() {
        return status;
    }
}
