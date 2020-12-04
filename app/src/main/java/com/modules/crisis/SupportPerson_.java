package com.modules.crisis;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 07-09-2017
 *         Last Modified on 16-11-2017
 */

public class SupportPerson_ {

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("relationship")
    private String relationship;

    @SerializedName("phone")
    private String phone;

    @SerializedName(General.STATUS)
    private int status;

    public void setName(String name) {
        this.name = name;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getPhone() {
        return phone;
    }

    public int getStatus() {
        return status;
    }
}
