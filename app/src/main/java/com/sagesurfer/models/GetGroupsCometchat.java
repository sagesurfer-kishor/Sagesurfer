package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

public class GetGroupsCometchat implements Serializable {

    @SerializedName("group_id")
    private String groupId;
    @SerializedName("type")
    private String type;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;

    @SerializedName("is_member")
    private Integer is_member;



    @SerializedName("is_friend")
    private Integer is_friend;

    @SerializedName("owner")
    private String owner;

    @SerializedName("created")
    private String created;

    @SerializedName("members_count")
    private String members_count;

    @SerializedName(General.MEMBERS)
    private ArrayList<Members_> membersArrayList;

    public Integer getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(Integer is_friend) {
        this.is_friend = is_friend;
    }


    public ArrayList<Members_> getMembersArrayList() {
        return membersArrayList;
    }

    public void setMembersArrayList(ArrayList<Members_> membersArrayList) {
        this.membersArrayList = membersArrayList;
    }

    public String getMembers_count() {
        return members_count;
    }

    public void setMembers_count(String members_count) {
        this.members_count = members_count;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getIs_member() {
        return is_member;
    }

    public void setIs_member(Integer is_member) {
        this.is_member = is_member;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    @SerializedName("status")
    private Integer status;

    @SerializedName("owner_id")
    private String owner_id;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
