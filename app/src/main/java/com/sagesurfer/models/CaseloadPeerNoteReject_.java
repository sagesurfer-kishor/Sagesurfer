package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 10/26/2018.
 */

public class CaseloadPeerNoteReject_ implements Serializable {
    @SerializedName(General.COMMENT)
    private String comment;

    @SerializedName(General.REASON_ID)
    private int reason_id;

    @SerializedName(General.NAME)
    private String name;

    private boolean selected;

    @SerializedName(General.STATUS)
    private int status;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getReason_id() {
        return reason_id;
    }

    public void setReason_id(int reason_id) {
        this.reason_id = reason_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
