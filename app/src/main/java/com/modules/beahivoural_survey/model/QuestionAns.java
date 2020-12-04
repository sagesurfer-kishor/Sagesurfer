package com.modules.beahivoural_survey.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class QuestionAns implements Serializable {

    @SerializedName(General.QUESTION)
    private String question;

    @SerializedName(General.ANSWER)
    private String answer;

}

