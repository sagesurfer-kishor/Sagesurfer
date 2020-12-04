package com.modules.consent;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 09-08-2017
 *         Last Modified on 16-11-2017
 */

public class ConsentFile_ implements Serializable{

    @SerializedName(General.ID)
    private long id;

    @SerializedName("realname")
    private String real_name;

    @SerializedName(General.SIZE)
    private long size;

    @SerializedName("note")
    private String note;

    @SerializedName("author_id")
    private long author_id;

    @SerializedName("author_name")
    private String author_name;

    @SerializedName(General.GROUP_ID)
    private long group_id;

    @SerializedName(General.GROUP_NAME)
    private String group_name;

    @SerializedName("youth_id")
    private long youth_id;

    @SerializedName("youth_name")
    private String youth_name;

    @SerializedName("shared_date")
    private long shared_date;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(long id) {
        this.id = id;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setRealname(String real_name) {
        this.real_name = real_name;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAuthor_id(long author_id) {
        this.author_id = author_id;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public void setYouth_id(long youth_id) {
        this.youth_id = youth_id;
    }

    public void setYouth_name(String youth_name) {
        this.youth_name = youth_name;
    }

    public void setShared_date(long shared_date) {
        this.shared_date = shared_date;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Method */

    public long getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public String getRealname() {
        return real_name;
    }

    public String getNote() {
        return note;
    }

    public long getAuthor_id() {
        return author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public long getGroup_id() {
        return group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public long getYouth_id() {
        return youth_id;
    }

    public String getYouth_name() {
        return youth_name;
    }

    public long getShared_date() {
        return shared_date;
    }

    public int getStatus() {
        return status;
    }
}
