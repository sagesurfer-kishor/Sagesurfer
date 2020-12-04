package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 7/10/2018.
 */

public class CaseloadProgressNote_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.BY_USER)
    private String by_user;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.SUBJECTIVE)
    private String subjective;

    @SerializedName(General.OBJECTIVE)
    private String objective;

    @SerializedName(General.ASSESSMENT)
    private String assessment;

    @SerializedName(General.PLAN)
    private String plan;

    @SerializedName(General.URL)
    private String url;

    @SerializedName(General.STATUS)
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBy_user() {
        return by_user;
    }

    public void setBy_user(String by_user) {
        this.by_user = by_user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubjective() {
        return subjective;
    }

    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
