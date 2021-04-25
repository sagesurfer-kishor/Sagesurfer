package com.modules.covid_19.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CovidTitleModel implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.QUESTIONS)
    private List<CovidModel> question;

    public CovidTitleModel() {
    }

    public CovidTitleModel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CovidModel> getQuestion() {
        return question;
    }

    public void setQuestion(List<CovidModel> question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

