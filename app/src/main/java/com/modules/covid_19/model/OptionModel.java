package com.modules.covid_19.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class OptionModel implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.ANSWER)
    private String answer;

    private int selectedRadioBtnId;
    private boolean isSelected;
    private String ansId;

    public OptionModel() {
    }

    public OptionModel(String id, String answer) {
        this.id = id;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getSelectedRadioBtnId() {
        return selectedRadioBtnId;
    }

    public void setSelectedRadioBtnId(int selectedRadioBtnId) {
        this.selectedRadioBtnId = selectedRadioBtnId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAnsId() {
        return ansId;
    }

    public void setAnsId(String ansId) {
        this.ansId = ansId;
    }
}
