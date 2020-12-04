package com.modules.caseload.senjam.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class SenjamListModel implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.ADDED_BY_ID)
    private String added_by_id;

    @SerializedName(General.SUBJECT)
    private String subject;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.TIME)
    private String time;

    @SerializedName(General.POSTED_DATE)
    private int posted_date;

    @SerializedName(General.UPDATED_DATE)
    private int updated_date;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.QUESTION)
    private String question;

    @SerializedName(General.ANSWER_ID)
    private String answer_id;

    @SerializedName(General.ANSWER_NAME)
    private String answer_name;

    @SerializedName(General.ANS_ID)
    private String ans_id;

    @SerializedName(General.ANS)
    private String ans;

    @SerializedName(General.ADDED_DATE)
    private int added_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getAnswer_name() {
        return answer_name;
    }

    public void setAnswer_name(String answer_name) {
        this.answer_name = answer_name;
    }

    public String getAdded_by_id() {
        return added_by_id;
    }

    public void setAdded_by_id(String added_by_id) {
        this.added_by_id = added_by_id;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public int getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(int updated_date) {
        this.updated_date = updated_date;
    }

    public String getAns_id() {
        return ans_id;
    }

    public void setAns_id(String ans_id) {
        this.ans_id = ans_id;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public int getAdded_date() {
        return added_date;
    }

    public void setAdded_date(int added_date) {
        this.added_date = added_date;
    }
}
