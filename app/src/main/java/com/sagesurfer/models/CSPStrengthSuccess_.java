package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 6/5/2018.
 */
//CaseloadSummaryProfileStrengthSuccess_
public class CSPStrengthSuccess_ implements Serializable {

    @SerializedName(General.DETAILS)
    private String details;

    @SerializedName(General.PATH)
    private String path;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
