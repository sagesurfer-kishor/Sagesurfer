package com.modules.appointment.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;
import com.sagesurfer.parser.Participant_;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 2/4/2020.
 */
public class Appointment_ implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.CLIENT_ID)
    private int client_id;

    @SerializedName(General.CLIENT_NAME)
    private String client_name;

    @SerializedName(General.DATE)
    private String date;

    @SerializedName(General.START_TIME)
    private String start_time;

    @SerializedName(General.END_TIME)
    private String end_time;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.APP_STATUS)
    private int app_status;

    @SerializedName(General.GROUP_ID)
    private int group_id;

    @SerializedName(General.GROUP_NAME)
    private String group_name;

    @SerializedName(General.ISAPPOINTMENTATTEND)
    private int isAppointmentAttend;

    @SerializedName(General.ADDED_BY_ID)
    private int added_by_id;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    @SerializedName(General.LAST_UPDATED)
    private long last_updated;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.REASON)
    private String reason;

    @SerializedName(General.ERROR)
    private String error;

    @SerializedName(General.EMAIL)
    private String email;

    @SerializedName(General.PHONE)
    private String phone = "";

    public String getYouth_yes_no() {
        return youth_yes_no;
    }

    public void setYouth_yes_no(String youth_yes_no) {
        this.youth_yes_no = youth_yes_no;
    }

    @SerializedName("youth_yes_no")
    private String youth_yes_no;


    @SerializedName("services")
    private ArrayList<Staff> services;

    @SerializedName("staff")
    private ArrayList<Staff> staff;

    @SerializedName("other_staff")
    private ArrayList<Staff> otherStaffs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getApp_status() {
        return app_status;
    }

    public void setApp_status(int app_status) {
        this.app_status = app_status;
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

    public int getIsAppointmentAttend() {
        return isAppointmentAttend;
    }

    public void setIsAppointmentAttend(int isAppointmentAttend) {
        this.isAppointmentAttend = isAppointmentAttend;
    }

    public int getAdded_by_id() {
        return added_by_id;
    }

    public void setAdded_by_id(int added_by_id) {
        this.added_by_id = added_by_id;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<Staff> getServices() {
        return services;
    }

    public void setServices(ArrayList<Staff> services) {
        this.services = services;
    }

    public ArrayList<Staff> getStaff() {
        return staff;
    }

    public void setStaff(ArrayList<Staff> staff) {
        this.staff = staff;
    }

    public ArrayList<Staff> getOtherStaffs() {
        return otherStaffs;
    }

    public void setOtherStaffs(ArrayList<Staff> otherStaffs) {
        this.otherStaffs = otherStaffs;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
