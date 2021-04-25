package com.sagesurfer.models;


import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class Teams_ implements Serializable {

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName("banner_img")
    private String banner;

    @SerializedName("group_id")
    private int id;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.OWNER_ID)
    private int owner_id;

    @SerializedName(General.MODERATOR)
    private int moderator;

    @SerializedName(General.PERMISSION)
    private int permission;

    @SerializedName(General.MEMBERS)
    private int members;

    @SerializedName(General.MEMBERS_LIST)
    private ArrayList<Members_> membersArrayList;

    @SerializedName(General.IS_MODERATOR)
    private int is_moderator;

    //For showing/differentiating between joined and my team on Team listing page
    @SerializedName(General.IS_JOINED_TEAM)
    private int is_joined_team;

    //For showing/differentiating between joined and my team on Team listing page
    @SerializedName(General.CONSUMER)
    private String consumer;

    @SerializedName(General.LASTVISIT)
    private String lastVisit;

    @SerializedName(General.LASTACTIVITY)
    private String lastActivity;

    @SerializedName(General.TYPE)
    private int type;

    @SerializedName("is_modarator")
    private int is_modarator;

    public String getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(String lastVisit) {
        this.lastVisit = lastVisit;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    private boolean isSelected = false;

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setOwnerId(int owner_id) {
        this.owner_id = owner_id;
    }

    public void setModerator(int moderator) {
        this.moderator = moderator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /* Getter methods */
    public int getStatus() {
        return this.status;
    }

    public int getId() {
        return this.id;
    }

    public int getOwnerId() {
        return this.owner_id;
    }

    public int getModerator() {
        return this.moderator;
    }

    public int getPermission() {
        return this.permission;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoto() {
        return this.photo;
    }

    public String getBanner() {
        return this.banner;
    }

    public int getMembers() {
        return this.members;
    }

    public boolean getSelected() {
        return this.isSelected;
    }

    public ArrayList<Members_> getMembersArrayList() {
        return membersArrayList;
    }

    public void setMembersArrayList(ArrayList<Members_> membersArrayList) {
        this.membersArrayList = membersArrayList;
    }

    public int getIs_moderator() {
        return is_moderator;
    }

    public void setIs_moderator(int is_moderator) {
        this.is_moderator = is_moderator;
    }

    public int getIs_joined_team() {
        return is_joined_team;
    }

    public void setIs_joined_team(int is_joined_team) {
        this.is_joined_team = is_joined_team;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIs_modarator() {
        return is_modarator;
    }

    public void setIs_modarator(int is_modarator) {
        this.is_modarator = is_modarator;
    }
}