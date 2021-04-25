package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 10/29/2018.
 */

public class CaseloadPeerNoteViewLog_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.NOTE_STATUS)
    private String note_status;

    @SerializedName(General.PROCESS)
    private String process;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.COMMENT)
    private String comment;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.STATUS)
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNote_status() {
        return note_status;
    }

    public void setNote_status(String note_status) {
        this.note_status = note_status;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
