package com.modules.fms;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author girish M (girish@sagesurfer.com)
 *         Created on 01/08/2017.
 *         Last Modified on 13/09/2017.
 **/

public class File_ {

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.GROUP_ID)
    private int group_id;

    @SerializedName(General.USER_ID)
    private int user_id;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.IS_DEFAULT)
    private int is_default;

    @SerializedName(General.CHECK_IN)//check_in
    private int check_in;

    @SerializedName(General.PERMISSION)
    private int permission;

    @SerializedName(General.POSTED_DATE)//posted_date
    private long posted_date;

    @SerializedName(General.TEAM_NAME)
    private String team_name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName(General.REAL_NAME)//realname
    private String real_name;

    @SerializedName(General.COMMENT)
    private String comment;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.SIZE)
    private long size;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGroupId(int group_id) {
        this.group_id = group_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setIsRead(int is_read) {
        this.is_read = is_read;
    }

    public void setIsDefault(int is_default) {
        this.is_default = is_default;
    }

    public void setCheckIn(int check_in) {
        this.check_in = check_in;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public void setDate(long posted_date) {
        this.posted_date = posted_date;
    }

    public void setTeamName(String team_name) {
        this.team_name = team_name;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public void setRealName(String real_name) {
        this.real_name = real_name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSize(long size) {
        this.size = size;
    }

    /*** Getter Methods ***/

    public int getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return group_id;
    }

    public int getUserId() {
        return user_id;
    }

    public int getIsRead() {
        return is_read;
    }

    public int getIsDefault() {
        return is_default;
    }

    public int getCheckIn() {
        return check_in;
    }

    public int getPermission() {
        return permission;
    }

    public long getDate() {
        return posted_date;
    }

    public String getTeamName() {
        return team_name;
    }

    public String getUserName() {
        return username;
    }

    public String getFullName() {
        return full_name;
    }

    public String getRealName() {
        return real_name;
    }

    public String getComment() {
        return comment;
    }

    public String getDescription() {
        return description;
    }

    public long getSize() {
        return size;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }
}
