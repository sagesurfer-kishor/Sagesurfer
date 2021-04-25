package com.modules.assessment;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Kailash Karankal
 * Created on 09-05-2019
 **/

public class Forms_ implements Serializable {

    @SerializedName("record_id")
    private long record_id;

    @SerializedName("form_id")
    private long form_id;

    @SerializedName("form_name")
    private String form_name;

    @SerializedName("sender")
    private String sender;

    @SerializedName(General.TIMESTAMP)
    private long timestamp;

    @SerializedName(General.ADDED_DATE)
    private long added_date;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.QUESTION)
    private String question;

    @SerializedName(General.ANSWER)
    private String answer;

    @SerializedName(General.COUNT)
    private String count;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setForm_id(long form_id) {
        this.form_id = form_id;
    }

    public void setRecord_id(long record_id) {
        this.record_id = record_id;
    }

    public void setForm_name(String form_name) {
        this.form_name = form_name;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Method */

    public long getForm_id() {
        return form_id;
    }

    public long getRecord_id() {
        return record_id;
    }

    public String getForm_name() {
        return form_name;
    }

    public String getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getAdded_date() {
        return added_date;
    }

    public void setAdded_date(long added_date) {
        this.added_date = added_date;
    }
}
