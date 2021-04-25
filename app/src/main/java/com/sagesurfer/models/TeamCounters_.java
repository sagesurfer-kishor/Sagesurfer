package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Monika on 6/12/2018.
 */

//"annoncement":"28","tasklist":0,"teamtalk":"8","calendar":"1","fms":9,"gallery":"1","poll":"0","members":4}
public class TeamCounters_ implements Serializable {

    @SerializedName(General.TEAM_ID)
    private int team_id;

    @SerializedName(General.ANNOUNCEMENT)
    private int announcement;

    @SerializedName(General.TASKLIST)
    private int tasklist;

    @SerializedName(General.TEAMTALK)
    private int teamtalk;

    @SerializedName(General.CALENDAR)
    private int calendar;

    @SerializedName(General.FMS)
    private int fms;

    @SerializedName(General.GALLERY)
    private int gallery;

    @SerializedName(General.POLL)
    private int poll;

    @SerializedName(General.MEMBERS)
    private int members;

    @SerializedName(General.MESSAGEBOARD)
    private int messageboard;

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public int getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(int announcement) {
        this.announcement = announcement;
    }

    public int getTasklist() {
        return tasklist;
    }

    public void setTasklist(int tasklist) {
        this.tasklist = tasklist;
    }

    public int getTeamtalk() {
        return teamtalk;
    }

    public void setTeamtalk(int teamtalk) {
        this.teamtalk = teamtalk;
    }

    public int getCalendar() {
        return calendar;
    }

    public void setCalendar(int calendar) {
        this.calendar = calendar;
    }

    public int getFms() {
        return fms;
    }

    public void setFms(int fms) {
        this.fms = fms;
    }

    public int getGallery() {
        return gallery;
    }

    public void setGallery(int gallery) {
        this.gallery = gallery;
    }

    public int getPoll() {
        return poll;
    }

    public void setPoll(int poll) {
        this.poll = poll;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getMessageboard() {
        return messageboard;
    }

    public void setMessageboard(int messageboard) {
        this.messageboard = messageboard;
    }
}
