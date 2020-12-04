package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Monika on 6/6/2018.
 */
//CaseloadSummaryGoals_
public class CSGoals_ implements Serializable {
    @SerializedName(General.GOALS)
    private List<String> goals;

    @SerializedName(General.PERSONAL_GOALS)
    private List<String> personal_goals;

    @SerializedName(General.STATUS)
    private int status;

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public List<String> getPersonal_goals() {
        return personal_goals;
    }

    public void setPersonal_goals(List<String> personal_goals) {
        this.personal_goals = personal_goals;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
