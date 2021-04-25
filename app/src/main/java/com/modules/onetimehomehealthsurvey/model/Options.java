package com.modules.onetimehomehealthsurvey.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class Options implements Serializable {

    @SerializedName(General.ANSWER)
    private String answer;

    @SerializedName(General.ID)
    private String id;


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
