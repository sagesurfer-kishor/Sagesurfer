package com.modules.re_assignment.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 12/20/2019.
 */
public class ReAssignment implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName("stud_array")
    private ArrayList<StudAssignData> studAssignDataArrayList = new ArrayList<>();

    @SerializedName(General.ADDED_DATE)
    private Long added_date;

    @SerializedName(General.REASON)
    private String reason;

    @SerializedName(General.ERROR)
    private String error;

    @SerializedName(General.STATUS)
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<StudAssignData> getStudAssignDataArrayList() {
        return studAssignDataArrayList;
    }

    public void setStudAssignDataArrayList(ArrayList<StudAssignData> studAssignDataArrayList) {
        this.studAssignDataArrayList = studAssignDataArrayList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getAdded_date() {
        return added_date;
    }

    public void setAdded_date(Long added_date) {
        this.added_date = added_date;
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
}
