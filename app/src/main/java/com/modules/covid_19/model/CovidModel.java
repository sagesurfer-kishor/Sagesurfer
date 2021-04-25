package com.modules.covid_19.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CovidModel implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.QUES)
    private String ques;

    @SerializedName(General.OPTIONS)
    private ArrayList<OptionModel> option;

    private int selectedRadioBtnId;
    private boolean isSelected;
    private String ansId;

    public CovidModel() {
    }

    public CovidModel(String id, String ques, ArrayList<OptionModel> option) {
        this.id = id;
        this.ques = ques;
        this.option = option;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<OptionModel> getOption() {
        return option;
    }

    public void setOption(ArrayList<OptionModel> option) {
        this.option = option;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }
}
