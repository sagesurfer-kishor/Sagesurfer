package com.modules.landing_question_form.module;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 7/16/2019.
 */
public class LandingQuestion_ implements Serializable {
    @SerializedName(General.QUESTION)
    private ArrayList<LandingQuestionsData_> question;

    @SerializedName(General.ANSWER)
    private ArrayList<LandingQuestionsAnswerData_> answer;

    private boolean isSelected = false;

    @SerializedName(General.STATUS)
    private int status;

    public ArrayList<LandingQuestionsData_> getQuestion() {
        return question;
    }

    public void setQuestion(ArrayList<LandingQuestionsData_> question) {
        this.question = question;
    }

    public ArrayList<LandingQuestionsAnswerData_> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList<LandingQuestionsAnswerData_> answer) {
        this.answer = answer;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
