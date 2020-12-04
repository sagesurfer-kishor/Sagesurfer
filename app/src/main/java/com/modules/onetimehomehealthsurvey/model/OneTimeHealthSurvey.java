package com.modules.onetimehomehealthsurvey.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

public class OneTimeHealthSurvey implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.QUES)
    private String ques;

    @SerializedName(General.OPTIONS)
    private ArrayList<Options> options;

    @SerializedName(General.HEADING)
    private String heading;

    @SerializedName(General.STATUS)
    private String status;

    private int selectedRadioBtnId;
    private boolean isSelected;
    private String ansId;

    public OneTimeHealthSurvey() {
    }

    public OneTimeHealthSurvey(String id, String ques, ArrayList<Options> options, String heading, String status, int selectedRadioBtnId, boolean isSelected, String ansId) {
        this.id = id;
        this.ques = ques;
        this.options = options;
        this.heading = heading;
        this.status = status;
        this.selectedRadioBtnId = selectedRadioBtnId;
        this.isSelected = isSelected;
        this.ansId = ansId;
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

    public ArrayList<Options> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Options> options) {
        this.options = options;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
