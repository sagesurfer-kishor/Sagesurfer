package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class UserInfo_ {

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName("firstname")
    private String first_name;

    @SerializedName("lastname")
    private String last_name;

    @SerializedName("userid")
    private String user_id;

    @SerializedName("gender")
    private String gender;

    @SerializedName("comet_chat_id")
    private String comet_chat_id;

    @SerializedName(General.EMAIL)
    private String email;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.ROLE)
    private String role;

    @SerializedName(General.ROLE_ID)
    private String role_id;

    @SerializedName(General.IS_REVIEWER)
    private int is_reviewer;

    @SerializedName(General.GROUP_ID)
    private String group_id;

    @SerializedName(General.GROUP_NAME)
    private String group_name;

    @SerializedName("login_log_id")
    private String login_log_id;

    @SerializedName("userTimezone")
    private String userTimezone;

    @SerializedName("client_id")
    private String client_id;

    @SerializedName("client_secret")
    private String client_secret;

    public String getComet_chat_id() {
        return comet_chat_id;
    }

    public void setComet_chat_id(String comet_chat_id) {
        this.comet_chat_id = comet_chat_id;
    }

    @SerializedName("profile_completion")
    private String profile_completion;

    @SerializedName(General.MOOD_REMINDER_STATUS)
    private String mood_reminder_status;

    public String getLanding_questions() {
        return landing_questions;
    }

    public void setLanding_questions(String landing_questions) {
        this.landing_questions = landing_questions;
    }

    @SerializedName(General.LANDING_QUESTIONS)
    private String landing_questions;

    @SerializedName(General.MOOD_SETTING)
    private String mood_setting;

    @SerializedName(General.LATI_LONGI)
    private String lati_longi;

    @SerializedName(General.PROFILE_LOCATION)
    private String profile_location;

    @SerializedName(General.CITY)
    private int city;

    @SerializedName(General.STATE)
    private long state;

    @SerializedName(General.COUNTRY)
    private long country;

    @SerializedName(General.DOB)
    private String dob;

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setGroupId(String group_id) {
        this.group_id = group_id;
    }

    public void setGroupName(String group_name) {
        this.group_name = group_name;
    }

    public void setClientId(String client_id) {
        this.client_id = client_id;
    }

    public void setClientSeceret(String client_secret) {
        this.client_secret = client_secret;
    }

    public void setIsReviewer(int is_reviewer) {
        this.is_reviewer = is_reviewer;
    }

    public void setProfileCompletion(String profile_completion) {
        this.profile_completion = profile_completion;
    }


    /*Setter*/
    public void setRoleId(String role_id) {
        this.role_id = role_id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getName() {
        return this.first_name + " " + this.last_name;
    }

    public String getFirstName() {
        return this.first_name;
    }

    public String getLastName() {
        return this.last_name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getImage() {
        return this.image;
    }

    public String getRole() {
        return this.role;
    }

    public String getRoleId() {
        return this.role_id;
    }

    public String getGroupId() {
        return this.group_id;
    }

    public String getGroupName() {
        return this.group_name;
    }

    public String getClientId() {
        return this.client_id;
    }

    public String getClientSecret() {
        return this.client_secret;
    }

    public int getIsReviewer() {
        return this.is_reviewer;
    }

    public String getProfileCompletion() {
        return this.profile_completion;
    }

    public String getMood_reminder_status() {
        return mood_reminder_status;
    }

    public void setMood_reminder_status(String mood_reminder_status) {
        this.mood_reminder_status = mood_reminder_status;
    }

    public String getMood_setting() {
        return mood_setting;
    }

    public void setMood_setting(String mood_setting) {
        this.mood_setting = mood_setting;
    }

    public String getLati_longi() {
        return lati_longi;
    }

    public void setLati_longi(String lati_longi) {
        this.lati_longi = lati_longi;
    }

    public String getProfile_location() {
        return profile_location;
    }

    public void setProfile_location(String profile_location) {
        this.profile_location = profile_location;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public long getCountry() {
        return country;
    }

    public void setCountry(long country) {
        this.country = country;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
