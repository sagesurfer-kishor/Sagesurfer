package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/11/2018
 * Last Modified on 4/11/2018
 */

public class Choices_ implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.SCHOOL_ID)
    private int school_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.FIRST_NAME)
    private String firstname;

    @SerializedName(General.MIDDLE_NAME)
    private String middlename;

    @SerializedName(General.LAST_NAME)
    private String lastname;

    @SerializedName(General.EMAIL)
    private String email;

    @SerializedName(General.DOB)
    private String dob;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.ROLE)
    private String role;

    @SerializedName(General.GRADE)
    private int grade;

    @SerializedName(General.TOTAL_ASSIGNED)
    private int total_assigned;

    @SerializedName(General.LAST_UPDATED)
    private long last_updated;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.IS_SELECTED)
    private String is_selected;

    @SerializedName("date_diff")
    private String date_diff;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIs_selected() {
        return is_selected;
    }

    public void setIs_selected(String is_selected) {
        this.is_selected = is_selected;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotal_assigned() {
        return total_assigned;
    }

    public void setTotal_assigned(int total_assigned) {
        this.total_assigned = total_assigned;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate_diff() {
        return date_diff;
    }

    public void setDate_diff(String date_diff) {
        this.date_diff = date_diff;
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }
}
