package com.sagesurfer.parser;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;
import com.sagesurfer.secure._Base64;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 17-08-2017
 * Last Modified on 16-11-2017
 */

public class Invitations_ implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.TIMESTAMP)
    private long timestamp;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.TEAM_ID)
    private long team_id;

    @SerializedName(General.TEAM_NAME)
    private String team_name;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.MESSAGE_ONE)
    private String msg;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @SerializedName(General.ERROR)
    private String error;


    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    private Boolean isSelected = false;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /* Setter Methods */

    public void setId(long id) {
        this.id = id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTeam_id(long team_id) {
        this.team_id = team_id;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Methods */

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return _Base64.decode(description);
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public long getTeam_id() {
        return team_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public int getIs_read() {
        return is_read;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public int getStatus() {
        return status;
    }

}
