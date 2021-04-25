package com.modules.sows.model;

import com.google.gson.annotations.SerializedName;
import com.modules.wall.Attachment_;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

public class SowsNotes implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.SUBJECT)
    private String subject;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.TIME)
    private String time;

    @SerializedName(General.POSTED_DATE)
    private int posted_date;

    @SerializedName(General.LAST_UPDATED)
    private int last_updated;

    @SerializedName(General.DB_ADD_DATE)
    private int db_add_date;

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

    @SerializedName(General.ATTACHMENTS)
    private ArrayList<Attachment_> attachmentList;

    @SerializedName(General.IS_FAV)
    private int is_fav;

    @SerializedName(General.MSG)
    private String msg;

    @SerializedName(General.ERROR)
    private String error;

    @SerializedName(General.STATUS)
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(int posted_date) {
        this.posted_date = posted_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(int last_updated) {
        this.last_updated = last_updated;
    }

    public int getDb_add_date() {
        return db_add_date;
    }

    public void setDb_add_date(int db_add_date) {
        this.db_add_date = db_add_date;
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

    public ArrayList<Attachment_> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(ArrayList<Attachment_> attachmentList) {
        this.attachmentList = attachmentList;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
