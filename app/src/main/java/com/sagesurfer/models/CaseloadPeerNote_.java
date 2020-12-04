package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Kailash
 */

public class CaseloadPeerNote_ implements Serializable {
    @SerializedName(General.VISIT_ID)
    private long visit_id;

    @SerializedName(General.SUBJECT)
    private String subject;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.VISIT_DATE)
    private String visit_date;

    @SerializedName(General.CONTACT_TYPE)
    private String contact_type;

    @SerializedName(General.AT_TIME)
    private String at_time;

    @SerializedName(General.DURATION)
    private String duration;

    @SerializedName(General.NEXT_STEP_NOTES)
    private String next_step_notes;

    @SerializedName(General.RESOURCES_NEEDED)
    private String resources_needed;

    @SerializedName(General.NOTES)
    private String notes;

    @SerializedName(General.IS_ON_LEAVE)
    private int is_on_leave;

    @SerializedName(General.REJECT_CNT)
    private int reject_cnt;

    @SerializedName(General.NO_OF_LEAVES)
    private int no_of_leaves;

    @SerializedName(General.MSG)
    private String msg;

    @SerializedName(General.TYPE)
    private String type;

    @SerializedName(General.PP_NAME)
    private String pp_name;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    @SerializedName(General.ADDED_BY_ID)
    private long added_by_id;

    @SerializedName(General.SUBMITTED_DATE)
    private long submitted_date;

    @SerializedName(General.APPROVED_DATE)
    private long approved_date;

    @SerializedName(General.NOTE_STATUS)
    private String note_status;

    @SerializedName(General.LOG_CNT)
    private String log_cnt;

    @SerializedName(General.AMENDMENT_CNT)
    private int amendment_cnt;

    @SerializedName(General.CONSUMER_ID)
    private long consumer_id;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("header_title")
    private String header_title;

    public long getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(long visit_id) {
        this.visit_id = visit_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getContact_type() {
        return contact_type;
    }

    public void setContact_type(String contact_type) {
        this.contact_type = contact_type;
    }

    public String getAt_time() {
        return at_time;
    }

    public void setAt_time(String at_time) {
        this.at_time = at_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNext_step_notes() {
        return next_step_notes;
    }

    public void setNext_step_notes(String next_step_notes) {
        this.next_step_notes = next_step_notes;
    }

    public String getResources_needed() {
        return resources_needed;
    }

    public void setResources_needed(String resources_needed) {
        this.resources_needed = resources_needed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getIs_on_leave() {
        return is_on_leave;
    }

    public void setIs_on_leave(int is_on_leave) {
        this.is_on_leave = is_on_leave;
    }

    public int getReject_cnt() {
        return reject_cnt;
    }

    public void setReject_cnt(int reject_cnt) {
        this.reject_cnt = reject_cnt;
    }

    public int getNo_of_leaves() {
        return no_of_leaves;
    }

    public void setNo_of_leaves(int no_of_leaves) {
        this.no_of_leaves = no_of_leaves;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNote_status() {
        return note_status;
    }

    public void setNote_status(String note_status) {
        this.note_status = note_status;
    }

    public String getLog_cnt() {
        return log_cnt;
    }

    public void setLog_cnt(String log_cnt) {
        this.log_cnt = log_cnt;
    }

    public int getAmendment_cnt() {
        return amendment_cnt;
    }

    public void setAmendment_cnt(int amendment_cnt) {
        this.amendment_cnt = amendment_cnt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getConsumer_id() {
        return consumer_id;
    }

    public void setConsumer_id(long consumer_id) {
        this.consumer_id = consumer_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPp_name() {
        return pp_name;
    }

    public void setPp_name(String pp_name) {
        this.pp_name = pp_name;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public long getAdded_by_id() {
        return added_by_id;
    }

    public void setAdded_by_id(long added_by_id) {
        this.added_by_id = added_by_id;
    }

    public long getSubmitted_date() {
        return submitted_date;
    }

    public void setSubmitted_date(long submitted_date) {
        this.submitted_date = submitted_date;
    }

    public long getApproved_date() {
        return approved_date;
    }

    public void setApproved_date(long approved_date) {
        this.approved_date = approved_date;
    }

    public String getHeader_title() {
        return header_title;
    }

    public void setHeader_title(String header_title) {
        this.header_title = header_title;
    }
}
