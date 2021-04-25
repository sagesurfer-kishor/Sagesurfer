package com.modules.teamtalk.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * Created by Kailash Karankal on 8/9/2019.
 */
public class ChildComments {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.MESSAGE)
    private String message;

    @SerializedName("posted_on")
    private long posted_on;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName("profile_pic")
    private String image;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("reply_id")
    private long reply_id;

    @SerializedName("user_id")
    private long user_id;

    @SerializedName("comment_cnt")
    private int comment_cnt;

    @SerializedName("child_cnt")
    private int child_cnt;

    private boolean selectMainLayout;

    private boolean isHighlight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getPosted_on() {
        return posted_on;
    }

    public void setPosted_on(long posted_on) {
        this.posted_on = posted_on;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getReply_id() {
        return reply_id;
    }

    public void setReply_id(long reply_id) {
        this.reply_id = reply_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
    }

    public int getChild_cnt() {
        return child_cnt;
    }

    public void setChild_cnt(int child_cnt) {
        this.child_cnt = child_cnt;
    }

    public boolean isSelectMainLayout() {
        return selectMainLayout;
    }

    public void setSelectMainLayout(boolean selectMainLayout) {
        this.selectMainLayout = selectMainLayout;
    }

    public boolean isHighlight() {
        return isHighlight;
    }

    public void setHighlight(boolean highlight) {
        isHighlight = highlight;
    }
}
