package com.modules.re_assignment.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Kailash Karankal on 12/20/2019.
 */
public class StudAssignData implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("assigned_date")
    private Long assigned_date;

    @SerializedName("reassignment_count")
    private int reassignment_count;

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

    public Long getAssigned_date() {
        return assigned_date;
    }

    public void setAssigned_date(Long assigned_date) {
        this.assigned_date = assigned_date;
    }

    public int getReassignment_count() {
        return reassignment_count;
    }

    public void setReassignment_count(int reassignment_count) {
        this.reassignment_count = reassignment_count;
    }
}
