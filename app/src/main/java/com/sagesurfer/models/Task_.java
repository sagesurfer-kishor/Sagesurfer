package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;
import com.sagesurfer.parser.Participant_;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class Task_ implements Serializable {

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.ID)
    private int id;

    @SerializedName("date")
    private long due_date;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.ADDED_BY)
    private String added_by;

    @SerializedName(General.TEAM_NAME)
    private String team_name;

    @SerializedName(General.TO_DO_STATUS)
    private String todo_status;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName("prio")
    private String priority;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName("team_id")
    private int team_id;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    @SerializedName(General.IS_OWNER)
    private int is_owner;

    @SerializedName(General.TYPE)
    private int type;

    @SerializedName(General.OWN_OR_TEAM)
    private int own_or_team;

    @SerializedName(General.LAST_UPDATED)
    private long last_updated;

    @SerializedName(General.C_DATE)
    private String c_date;

    @SerializedName(General.PARTICIPANTS)//participants
    private ArrayList<Participant_> participants;

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDate(long due_date) {
        this.due_date = due_date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddedBy(String added_by) {
        this.added_by = added_by;
    }

    public void setTeamId(int team_id) {
        this.team_id = team_id;
    }

    public void setTeamName(String team_name) {
        this.team_name = team_name;
    }

    public void setToDoStatus(String todo_status) {
        this.todo_status = todo_status;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setIsRead(int is_read) {
        this.is_read = is_read;
    }

    public void setIsDelete(int is_delete) {
        this.is_delete = is_delete;
    }

    public void setIsOwner(int is_owner) {
        this.is_owner = is_owner;
    }

    public void setOwnOrTeam(int own_or_team) {
        this.own_or_team = own_or_team;
    }

    public void setType(int type) {
        this.type = type;
    }

    /* Getter Method */

    public int getStatus() {
        return this.status;
    }

    public int getId() {
        return this.id;
    }

    public long getDate() {
        return this.due_date;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAddedBy() {
        return this.added_by;
    }

    public String getName() {
        return name;
    }

    public String getTeamName() {
        return this.team_name;
    }

    public String getToDoStatus() {
        return this.todo_status;
    }

    public String getFullName() {
        return this.full_name;
    }

    public String getPriority() {
        return this.priority;
    }

    public String getImage() {
        return this.image;
    }

    public int getTeamId() {
        return this.team_id;
    }

    public int getIsRead() {
        return this.is_read;
    }

    public int getIsDelete() {
        return this.is_delete;
    }

    public int getIsOwner() {
        return this.is_owner;
    }

    public int getOwnOrTeam() {
        return this.own_or_team;
    }

    public int getType() {
        return type;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public String getC_date() {
        return c_date;
    }

    public void setC_date(String c_date) {
        this.c_date = c_date;
    }

    public ArrayList<Participant_> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant_> participants) {
        this.participants = participants;
    }
}
