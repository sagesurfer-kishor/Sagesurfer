package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 10/29/2018.
 */

public class CaseloadPeerNoteComments_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.SUBJECT)
    private String subject;

    @SerializedName(General.COMMENTED_BY)
    private String commented_by;

    @SerializedName(General.COMMENT_DATE)
    private String comment_date;

    @SerializedName(General.COMMENT_REASON_ID)
    private String comment_reason_id;

    @SerializedName(General.REASONS)
    private String reasons;

    @SerializedName(General.COMMENTS)
    private String comments;

    @SerializedName(General.STATUS)
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCommented_by() {
        return commented_by;
    }

    public void setCommented_by(String commented_by) {
        this.commented_by = commented_by;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public String getComment_reason_id() {
        return comment_reason_id;
    }

    public void setComment_reason_id(String comment_reason_id) {
        this.comment_reason_id = comment_reason_id;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
