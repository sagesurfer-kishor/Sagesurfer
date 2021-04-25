package com.modules.platform;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 02-08-2017
 * Last Modified on 14-12-2017
 */


public class DashboardMessage_ {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.SUBJECT)
    private String subject;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.START_DATE)
    private long start_date;

    @SerializedName(General.END_DATE)
    private long end_date;

    @SerializedName(General.PRIORITY)
    private int priority;

    @SerializedName(General.CREATE_DATE)
    private long created_date;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.STATUS)
    private int status;

    /*** Setter Method ***/

    public void setId(int id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(long start_date) {
        this.start_date = start_date;
    }

    public void setEndDate(long end_date) {
        this.end_date = end_date;
    }

    public void setCreatedDate(long created_date) {
        this.created_date = created_date;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setIsRead(int is_read) {
        this.is_read = is_read;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*** Getter Methods ***/

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public long getStartDate() {
        return start_date;
    }

    public long getEndDate() {
        return end_date;
    }

    public long getCreatedDate() {
        return created_date;
    }

    public int getPriority() {
        return priority;
    }

    public int getIsRead() {
        return is_read;
    }

    public int getStatus() {
        return status;
    }
}

