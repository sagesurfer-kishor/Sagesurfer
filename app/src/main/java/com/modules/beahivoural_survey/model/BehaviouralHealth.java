package com.modules.beahivoural_survey.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class BehaviouralHealth implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("ques1")
    private String ques1;

    @SerializedName("ans1")
    private String ans1;

    @SerializedName("ques2")
    private String ques2;

    @SerializedName("ans2")
    private String ans2;

    @SerializedName("ques3")
    private String ques3;

    @SerializedName("ans3")
    private String ans3;

    @SerializedName(General.SUBMITTED_DATE)
    private long submitted_date;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.IS_SELECTED)
    private String is_selected;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public long getSubmitted_date() {
        return submitted_date;
    }

    public void setSubmitted_date(long submitted_date) {
        this.submitted_date = submitted_date;
    }

    public String getQues1() {
        return ques1;
    }

    public void setQues1(String ques1) {
        this.ques1 = ques1;
    }

    public String getAns1() {
        return ans1;
    }

    public void setAns1(String ans1) {
        this.ans1 = ans1;
    }

    public String getQues2() {
        return ques2;
    }

    public void setQues2(String ques2) {
        this.ques2 = ques2;
    }

    public String getAns2() {
        return ans2;
    }

    public void setAns2(String ans2) {
        this.ans2 = ans2;
    }

    public String getQues3() {
        return ques3;
    }

    public void setQues3(String ques3) {
        this.ques3 = ques3;
    }

    public String getAns3() {
        return ans3;
    }

    public void setAns3(String ans3) {
        this.ans3 = ans3;
    }

    public String getIs_selected() {
        return is_selected;
    }

    public void setIs_selected(String is_selected) {
        this.is_selected = is_selected;
    }
}

