package com.modules.assignment.werhope.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

public class School implements Serializable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.SCHOOL_ID)
    private int school_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.ADDRESS)
    private String address;

    @SerializedName(General.COUNTRY)
    private String country;

    @SerializedName(General.STATE)
    private String state;

    @SerializedName(General.CITY)
    private String city;

    @SerializedName(General.DISTRICT)
    private String district;

    @SerializedName(General.DATE)
    private long date;

    @SerializedName(General.STATUS)
    private int status;


    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}