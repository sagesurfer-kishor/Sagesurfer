package com.modules.landing_question_form.module;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Kailash Karankal on 7/16/2019.
 */
public class LandingQuestionsAnswerData_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.ANSWER)
    private String answer;

    @SerializedName(General.VALUE)
    private int value;

    private boolean isSelected = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

