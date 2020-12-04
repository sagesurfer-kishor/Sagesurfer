package com.modules.landing_question_form.module;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Kailash Karankal on 7/16/2019.
 */
public class LandingQuestionsData_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.QUES)
    private String ques;

    @SerializedName(General.STATUS)
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
