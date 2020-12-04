package com.modules.sos;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 17-07-2017
 * Last Modified on 14-12-2017
 */

public class ReceivedSos_ {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.MSG)
    private String message;

    @SerializedName(General.TIMESTAMP)
    private long time;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.CREATED_BY)
    private String created_by;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName(General.TYPE)
    private int type;

    @SerializedName(General.IS_ACTIVE_CONTACT)
    private int is_active_contact;

    @SerializedName(General.CURRENT_STATUS)
    private int current_status;

    @SerializedName(General.GROUP_ID)
    private int group_id;

    @SerializedName("group_role_id")
    private int group_role_id;

    @SerializedName(General.GROUP_NAME)
    private String group_name;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.USER_STATUS)
    private int user_status;

    @SerializedName(General.TRACK_WORDS)
    private String track_words;

    @SerializedName(General.PRIORITY)
    private int priority;

    @SerializedName("e1")
    private SosStatus_ e1;

    @SerializedName("e2")
    private SosStatus_ e2;

    @SerializedName("e3")
    private SosStatus_ e3;

    @SerializedName("cc")
    private SosStatus_ cc;

    @SerializedName(General.LOCATION)
    private String location;

    private boolean isNotAttending = false;

    /*** Setter Method ***/

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setIsRead(int is_read) {
        this.is_read = is_read;
    }

    public void setCreatedBy(String created_by) {
        this.created_by = created_by;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setActiveContact(int is_active_contact) {
        this.is_active_contact = is_active_contact;
    }

    public void setIsNotAttending(boolean isNotAttending) {
        this.isNotAttending = isNotAttending;
    }

    public void setCurrentStatus(int current_status) {
        this.current_status = current_status;
    }

    public void setGroupId(int group_id) {
        this.group_id = group_id;
    }

    public void setGroupRoleId(int group_role_id) {
        this.group_role_id = group_role_id;
    }

    public void setGroupName(String group_name) {
        this.group_name = group_name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /*** Getter Method ***/

    public int getId() {
        return this.id;
    }

    public String getMessage() {
        return this.message;
    }

    public long getTime() {
        return this.time;
    }

    public int getIsRead() {
        return this.is_read;
    }

    public String getCreatedBy() {
        return this.created_by;
    }

    public String getFullName() {
        return this.full_name;
    }

    public int getType() {
        return this.type;
    }

    public int getIsActive() {
        return this.is_active_contact;
    }

    public int getCurrentStatus() {
        return this.current_status;
    }

    public int getGroupId() {
        return this.group_id;
    }

    public String getGroupName() {
        return this.group_name;
    }

    public int getStatus() {
        return this.status;
    }

    public String getImage() {
        return this.image;
    }

    public int getGroupRoleId() {
        return this.group_role_id;
    }

    public boolean getIsNotAttending() {
        return this.isNotAttending;
    }

    public int getUser_status() {
        return user_status;
    }

    public void setUser_status(int user_status) {
        this.user_status = user_status;
    }


    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public int getIs_active_contact() {
        return is_active_contact;
    }

    public void setIs_active_contact(int is_active_contact) {
        this.is_active_contact = is_active_contact;
    }

    public int getCurrent_status() {
        return current_status;
    }

    public void setCurrent_status(int current_status) {
        this.current_status = current_status;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getGroup_role_id() {
        return group_role_id;
    }

    public void setGroup_role_id(int group_role_id) {
        this.group_role_id = group_role_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public boolean isNotAttending() {
        return isNotAttending;
    }

    public void setNotAttending(boolean notAttending) {
        isNotAttending = notAttending;
    }

    public String getTrack_words() {
        return track_words;
    }

    public void setTrack_words(String track_words) {
        this.track_words = track_words;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public SosStatus_ getE1() {
        return e1;
    }

    public void setE1(SosStatus_ e1) {
        this.e1 = e1;
    }

    public SosStatus_ getE2() {
        return e2;
    }

    public void setE2(SosStatus_ e2) {
        this.e2 = e2;
    }

    public SosStatus_ getE3() {
        return e3;
    }

    public void setE3(SosStatus_ e3) {
        this.e3 = e3;
    }

    public SosStatus_ getCc() {
        return cc;
    }

    public void setCc(SosStatus_ cc) {
        this.cc = cc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
