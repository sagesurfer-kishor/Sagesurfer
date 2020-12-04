package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Monika on 6/5/2018.
 */
//CaseloadSummaryProfile_
public class CSProfile_ implements Serializable{

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.ADDRESS)
    private String address;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.CATEGORY)
    private List<CSPCategory_> category;

    @SerializedName(General.STRENGTH)
    private List<CSPStrengthSuccess_> strength;

    @SerializedName(General.SUCCESS)
    private List<CSPStrengthSuccess_> success;

    @SerializedName(General.STATUS)
    private int status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<CSPCategory_> getCategory() {
        return category;
    }

    public void setCategory(List<CSPCategory_> category) {
        this.category = category;
    }

    public List<CSPStrengthSuccess_> getStrength() {
        return strength;
    }

    public void setStrength(List<CSPStrengthSuccess_> strength) {
        this.strength = strength;
    }

    public List<CSPStrengthSuccess_> getSuccess() {
        return success;
    }

    public void setSuccess(List<CSPStrengthSuccess_> success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
