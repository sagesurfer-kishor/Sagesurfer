package com.modules.blog;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 21-09-2017
 *         Last Modified on 16-11-2017
 */


public class Blog_ implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName("posted_by")
    private String name;

    @SerializedName("modified_on")
    private long date;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }
}
