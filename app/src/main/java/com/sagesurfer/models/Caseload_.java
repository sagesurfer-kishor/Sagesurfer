package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 6/1/2018.
 */

public class Caseload_ implements Serializable {
    @SerializedName(General.USER_ID)
    private long user_id;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.ID)
    private String id;

    @SerializedName("uname")
    private String uname;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.PHONE)
    private String phone;

    @SerializedName(General.GRADE)//team id
    private int grade;

    @SerializedName(General.DOB)
    private String dob;

    @SerializedName(General.GROUP_ID)//team id
    private int group_id;

    @SerializedName(General.GROUP_NAME)//team name
    private String group_name;

    @SerializedName(General.SCHOOL_ID)//team id
    private int school_id;

    @SerializedName(General.SCHOLL_NAME)//team name
    private String school_name;

    @SerializedName(General.PARENT_ID)//team id
    private int parent_id;

    @SerializedName(General.PARENT_NAME)//team name
    private String parent_name;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.ADDRESS)
    private String address;

    @SerializedName("last_progress_date")
    private String last_progress_date;

    @SerializedName("last_mood")
    private String last_mood;


    @SerializedName(General.MOOD_IMAGE)
    private String mood_image = "";

    @SerializedName(General.BANNER_IMG)//team banner image
    private String banner_img;

    @SerializedName(General.CONTACTED_LAST)
    private String contacted_last;

    @SerializedName(General.TYPE)
    private String type;

    @SerializedName(General.CONTACTED_LAST_TYPE)
    private String contacted_last_type;

    @SerializedName(General.LAST_TEAM_INTERACTION_TYPE)
    private String last_team_interaction_type;

    @SerializedName(General.LAST_TEAM_INTERACTION)
    private String last_team_interaction;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.COVID)
    private String covid;

    @SerializedName(General.DAILY_DOSING_STATUS)
    private int daily_dosing_status;

    public String getStaff_last_contacted() {
        return staff_last_contacted;
    }

    public void setStaff_last_contacted(String staff_last_contacted) {
        this.staff_last_contacted = staff_last_contacted;
    }

    @SerializedName(General.STAFF_LAST_CONTACTED)
    private String staff_last_contacted;

    private boolean selected;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContacted_last() {
        return contacted_last;
    }

    public void setContacted_last(String contacted_last) {
        this.contacted_last = contacted_last;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBanner_img() {
        return banner_img;
    }

    public void setBanner_img(String banner_img) {
        this.banner_img = banner_img;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getMood_image() {
        return mood_image;
    }

    public void setMood_image(String mood_image) {
        this.mood_image = mood_image;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getContacted_last_type() {
        return contacted_last_type;
    }

    public void setContacted_last_type(String contacted_last_type) {
        this.contacted_last_type = contacted_last_type;
    }

    public String getLast_team_interaction_type() {
        return last_team_interaction_type;
    }

    public void setLast_team_interaction_type(String last_team_interaction_type) {
        this.last_team_interaction_type = last_team_interaction_type;
    }

    public String getLast_team_interaction() {
        return last_team_interaction;
    }

    public void setLast_team_interaction(String last_team_interaction) {
        this.last_team_interaction = last_team_interaction;
    }

    public String getLast_progress_date() {
        return last_progress_date;
    }

    public void setLast_progress_date(String last_progress_date) {
        this.last_progress_date = last_progress_date;
    }

    public String getLast_mood() {
        return last_mood;
    }

    public void setLast_mood(String last_mood) {
        this.last_mood = last_mood;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCovid() {
        return covid;
    }

    public void setCovid(String covid) {
        this.covid = covid;
    }

    public int getDaily_dosing_status() {
        return daily_dosing_status;
    }

    public void setDaily_dosing_status(int daily_dosing_status) {
        this.daily_dosing_status = daily_dosing_status;
    }
}
