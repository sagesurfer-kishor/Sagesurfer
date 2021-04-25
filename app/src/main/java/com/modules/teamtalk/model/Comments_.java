package com.modules.teamtalk.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.util.ArrayList;

/**
 * @author Kailash Karankal
 */
public class Comments_ {

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

    @SerializedName("comment_count")
    private int comment_count;

    private boolean isHighlight;

    private ArrayList<ChildComments> childComments=new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*** Setter Method***/
    public void setStatus(int status) {
        this.status = status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String full_name) {
        this.full_name = full_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDate(long posted_on) {
        this.posted_on = posted_on;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /*** Getter Method ***/
    public int getStatus() {
        return this.status;
    }

    public String getImage() {
        return this.image;
    }

    public String getName() {
        return this.full_name;
    }

    public String getUsernmae() {
        return this.username;
    }

    public long getDate() {
        return this.posted_on;
    }

    public String getMessage() {
        return this.message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public long getReply_id() {
        return reply_id;
    }

    public void setReply_id(long reply_id) {
        this.reply_id = reply_id;
    }

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public ArrayList<ChildComments> getChildComments() {
        return childComments;
    }

    public void setChildComments(ArrayList<ChildComments> childComments) {
        this.childComments = childComments;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public boolean isHighlight() {
        return isHighlight;
    }

    public void setHighlight(boolean highlight) {
        isHighlight = highlight;
    }
}
