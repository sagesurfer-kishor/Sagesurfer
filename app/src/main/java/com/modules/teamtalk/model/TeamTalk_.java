package com.modules.teamtalk.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.HtmlRemover;

import java.io.Serializable;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 25-07-2017
 *         Last Modified on 15-12-2017
 */

public class TeamTalk_ implements Serializable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.MESSAGE)
    private String message;

    @SerializedName(General.TEAM_ID)
    private int team_id;

    @SerializedName(General.TEAM_NAME)
    private String team_name;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.LAST_UPDATED)
    private long last_updated;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.COUNT)
    private int count;

    /*** Setter Methods ***/

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTeamId(int team_id) {
        this.team_id = team_id;
    }

    public void setTeamName(String team_name) {
        this.team_name = team_name;
    }

    public void setAddedBy(String added_by) {
        this.added_by = added_by;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDate(long last_updated) {
        this.last_updated = last_updated;
    }

    public void setIsRead(int is_read) {
        this.is_read = is_read;
    }

    public void setIsDelete(int is_delete) {
        this.is_delete = is_delete;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /*** Getter Methods ***/

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return HtmlRemover.stripHtml(this.message);
    }

    public int getTeamId() {
        return this.team_id;
    }

    public String getTeamName() {
        return this.team_name;
    }

    public String getAddedby() {
        return this.added_by;
    }

    public String getFullName() {
        return this.full_name;
    }

    public String getImage() {
        return this.image;
    }

    public long getDate() {
        return this.last_updated;
    }

    public int getIsRead() {
        return this.is_read;
    }

    public int getIsDelete() {
        return this.is_delete;
    }

    public int getStatus() {
        return this.status;
    }

    public int getCount() {
        return this.count;
    }
}
