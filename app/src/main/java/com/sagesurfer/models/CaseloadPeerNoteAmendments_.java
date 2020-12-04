package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 10/29/2018.
 */

public class CaseloadPeerNoteAmendments_ implements Serializable {
    @SerializedName(General.AB_ID)
    private int ab_id;

    @SerializedName(General.NOTE_ID)
    private int note_id;

    @SerializedName(General.NOTE_SUBJECT)
    private String note_subject;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    @SerializedName(General.AMENDMENT)
    private String amendment;

    @SerializedName(General.ADDED_DATE)
    private String added_date;

    @SerializedName(General.IS_ACTIVE)
    private int is_active;

    @SerializedName(General.AMENDMENT_TXT)
    private String amendment_txt;

    @SerializedName(General.STATUS)
    private int status;

    public int getAb_id() {
        return ab_id;
    }

    public void setAb_id(int ab_id) {
        this.ab_id = ab_id;
    }

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public String getNote_subject() {
        return note_subject;
    }

    public void setNote_subject(String note_subject) {
        this.note_subject = note_subject;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getAmendment() {
        return amendment;
    }

    public void setAmendment(String amendment) {
        this.amendment = amendment;
    }

    public String getAdded_date() {
        return added_date;
    }

    public void setAdded_date(String added_date) {
        this.added_date = added_date;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public String getAmendment_txt() {
        return amendment_txt;
    }

    public void setAmendment_txt(String amendment_txt) {
        this.amendment_txt = amendment_txt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
