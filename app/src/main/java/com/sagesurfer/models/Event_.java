package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;
import com.sagesurfer.secure._Base64;

import java.io.Serializable;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 31-03-2018
 *         Last Modified on
 */


public class Event_ implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.TIMESTAMP)
    private long timestamp;

    @SerializedName(General.EVENT_TIME)//event_time
    private String event_time;

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

    @SerializedName(General.DATE_STRING)//date_string
    private String date_string;

    @SerializedName(General.PARTICIPANTS)//participants
    private int participants;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    @SerializedName(General.LOCATION)
    private String location;

    @SerializedName(General.STATUS)
    private int status;

    /* Setter Methods */

    public void setId(long id) {
        this.id = id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
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

    public void setDate_string(String date_string) {
        this.date_string = date_string;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    /* Getter Methods */

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getEvent_time() {
        return event_time;
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

    public String getDate_string() {
        return date_string;
    }

    public int getParticipants() {
        return participants;
    }

    public int getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
