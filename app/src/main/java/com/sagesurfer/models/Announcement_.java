package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class Announcement_ implements Serializable {

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.LAST_UPDATED)
    private long last_updated;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.CREATED_BY)
    private String created_by;

    @SerializedName(General.TEAM_NAME)
    private String team_name;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.TEAM_ID)
    private int team_id;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    @SerializedName(General.TYPE)
    private int type;

    @SerializedName(General.IMAGE)
    private String image;

    /* Setter Method */
    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(long last_updated) {
        this.last_updated = last_updated;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedBy(String created_by) {
        this.created_by = created_by;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTeamName(String team_name) {
        this.team_name = team_name;
    }

    public void setIsRead(int is_read) {
        this.is_read = is_read;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public void setIsDelete(int is_delete) {
        this.is_delete = is_delete;
    }

    public void setType(int type) {
        this.type = type;
    }

    /*Getter Method*/

    public int getStatus() {
        return this.status;
    }

    public int getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public long getDate() {
        return this.last_updated;
    }

    public String getUsername() {
        return this.username;
    }

    public String getCreatedBy() {
        return this.created_by;
    }

    public String getTeamName() {
        return this.team_name;
    }

    public String getImage() {
        return this.image;
    }

    public int getIsRead() {
        return this.is_read;
    }

    public int getTeamId() {
        return this.team_id;
    }

    public int getIsDelete() {
        return this.is_delete;
    }

    public int getType() {
        return type;
    }
}
