package com.modules.crisis;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 07-09-2017
 *         Last Modified on 16-11-2017
 */
public class Risk_ {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getStatus() {
        return status;
    }
}
