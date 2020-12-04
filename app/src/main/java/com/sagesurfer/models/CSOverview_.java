package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 6/5/2018.
 */

//CaseloadSummaryOverview_
public class CSOverview_ implements Serializable {

    @SerializedName(General.SUMMARY)
    private String summary;

    @SerializedName(General.BACKGROUND)
    private String background;

    @SerializedName(General.STATUS)
    private int status;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
