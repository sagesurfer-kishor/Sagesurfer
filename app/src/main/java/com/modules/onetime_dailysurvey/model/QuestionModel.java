package com.modules.onetime_dailysurvey.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionModel implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.QUES)
    private String ques;

    @SerializedName(General.ANS_ID)
    private String ans_id;

    @SerializedName(General.ANSWER)
    private String answer;

    @SerializedName(General.OPTIONS)
    private ArrayList<QuestionModel> options;

    public QuestionModel() {
    }

    public QuestionModel(String id, String ques, String ans_id, String answer) {
        this.id = id;
        this.ques = ques;
        this.ans_id = ans_id;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public String getAns_id() {
        return ans_id;
    }

    public void setAns_id(String ans_id) {
        this.ans_id = ans_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<QuestionModel> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<QuestionModel> options) {
        this.options = options;
    }
}
