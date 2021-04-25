package com.modules.onetime_dailysurvey.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

public class DailySurveyModel implements Serializable {

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.QUESTIONS)
    private ArrayList<QuestionModel> questions;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.ADDED_DATE)
    private int added_date;

    public DailySurveyModel() {
    }

    public DailySurveyModel(String title, String description, ArrayList<QuestionModel> questions, int status, int added_date) {
        this.title = title;
        this.description = description;
        this.questions = questions;
        this.status = status;
        this.added_date = added_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<QuestionModel> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuestionModel> questions) {
        this.questions = questions;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAdded_date() {
        return added_date;
    }

    public void setAdded_date(int added_date) {
        this.added_date = added_date;
    }
}
