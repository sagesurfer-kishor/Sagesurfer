package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * Created by Monika on 8/21/2018.
 */

public class PORActivityData_ {
    @SerializedName(General.COUNT)
    private int count;

    @SerializedName(General.MEMBER_COUNT)
    private int member_count;

    @SerializedName(General.GROUP_ID)
    private long group_id;

    @SerializedName(General.GROUP_NAME)
    private String group_name;

    @SerializedName(General.STATUS)
    private int status;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMember_count() {
        return member_count;
    }

    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
