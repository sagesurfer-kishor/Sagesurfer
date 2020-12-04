package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 07-08-2017
 *         Last Modified on 14-12-2017
 */

public class Video_ {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName("video_name")
    private String video_name;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("video_path")
    private String video_path;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName(General.STATUS)
    private int status;

    private void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVideoName(String video_name) {
        this.video_name = video_name;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setVideoPath(String video_path) {
        this.video_path = video_path;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    /* Getter Method */

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoName() {
        return video_name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVideoPath() {
        return video_path;
    }

    public String getFullName() {
        return full_name;
    }

    public int getStatus() {
        return status;
    }
}
