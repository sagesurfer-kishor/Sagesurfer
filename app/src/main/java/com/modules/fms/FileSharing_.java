package com.modules.fms;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author girish M (girish@sagesurfer.com)
 *         Created on 01/08/2017.
 *         Last Modified on 13/09/2017.
 **/


public class FileSharing_ implements Serializable{

    //Folder Variable
    @SerializedName(General.NAME)
    private String name;

    @SerializedName("total_files")
    private int total_files;

    @SerializedName("total_folders")
    private int total_folders;

    // File Variable
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

    @SerializedName("check_in")
    private int check_in;

    @SerializedName(General.PERMISSION)
    private int permission;

    @SerializedName("posted_date")
    private long posted_date;

    @SerializedName(General.TEAM_NAME)
    private String team_name;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName("realname")
    private String real_name;

    @SerializedName(General.COMMENT)
    private String comment;

    @SerializedName(General.DESCRIPTION)
    private String description;

    private boolean isSection;

    private boolean isFile;

    @SerializedName(General.SIZE)
    private long size;

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public void setSection(boolean isSection) {
        this.isSection = isSection;
    }

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

    public boolean isFile() {
        return this.isFile;
    }

    public boolean getIsSection() {
        return this.isSection;
    }

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

    //Folder getter setter methods

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalFiles(int total_files) {
        this.total_files = total_files;
    }

    public void setTotalFolders(int total_folders) {
        this.total_folders = total_folders;
    }

    /*** Getter Methods ***/

    public String getName() {
        return name;
    }

    public int getTotalFiles() {
        return total_files;
    }

    public int getTotalFolders() {
        return total_folders;
    }
}
