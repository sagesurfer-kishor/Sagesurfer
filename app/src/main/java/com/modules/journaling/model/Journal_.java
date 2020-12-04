package com.modules.journaling.model;

import com.google.gson.annotations.SerializedName;
import com.modules.wall.Attachment_;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 10/9/2019.
 */
public class Journal_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.SUBJECT)
    private String subject;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.TAGS)
    private String tags;

    @SerializedName(General.LATITUDE)
    private String latitude;

    @SerializedName(General.LONGITUDE)
    private String longitude;

    @SerializedName(General.LOCATION)
    private String location;

    @SerializedName(General.LINK)
    private String link;

    @SerializedName(General.DB_ADD_DATE)
    private long db_add_date;

    @SerializedName(General.IS_FAV)
    private int is_fav;

    @SerializedName(General.MSG)
    private String msg;

    @SerializedName(General.ERROR)
    private String error;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.ATTACHMENTS)
    private ArrayList<Attachment_> attachmentList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getDb_add_date() {
        return db_add_date;
    }

    public void setDb_add_date(long db_add_date) {
        this.db_add_date = db_add_date;
    }

    public int getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(int is_fav) {
        this.is_fav = is_fav;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public ArrayList<Attachment_> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(ArrayList<Attachment_> attachmentList) {
        this.attachmentList = attachmentList;
    }
}
