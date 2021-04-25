package com.sagesurfer.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 07-08-2017
 *         Last Modified on 15-12-2017
 */

public class Contacts_ implements Serializable ,Comparable{

    @SerializedName("firstname")
    private String first_name;

    @SerializedName("lastname")
    private String last_name;

    @SerializedName("home")
    private String home;

    @SerializedName("work")
    private String work;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName(General.EMAIL)
    private String email;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.ROLE)
    private String role;

    @SerializedName("youth")
    private int youth;

    @SerializedName(General.STATUS)
    private int status;


    /* Setter Method */

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setYouth(int youth) {
        this.youth = youth;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Method */

    public String getFirstName() {
        return first_name; //return first_name.toUpperCase();
    }

    public String getLastName() {
        return last_name;
    }

    public String getHome() {
        return home;
    }

    public String getWork() {
        return work;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getRole() {
        return role;
    }

    public int getYouth() {
        return youth;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}
