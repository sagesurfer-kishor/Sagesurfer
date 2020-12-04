package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 12/24/2018.
 */

public class HomeRecentUpdates_ implements Serializable {
    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.TYPE)
    private String type;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    @SerializedName(General.PROFILE)
    private String profile;

    @SerializedName(General.GROUP_NAME)
    private String group_name;

    @SerializedName(General.GROUP_ID)
    private Long group_id;

    @SerializedName(General.TIMESTAMP)
    private long timestamp;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.MODULE)
    private String module;

    @SerializedName(General.DESCRIPTION)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
