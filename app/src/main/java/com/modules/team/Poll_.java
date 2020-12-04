package com.modules.team;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 07-08-2017
 *         Last Modified on 14-12-2017
 */

class Options_ {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.COUNT)
    private int count;

    @SerializedName("is_selected")
    private int is_selected;

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setIs_selected(int is_selected) {
        this.is_selected = is_selected;
    }


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getCount() {
        return count;
    }

    public int getIs_selected() {
        return is_selected;
    }
}

public class Poll_ {

    @SerializedName(General.ID)
    private long id;

    @SerializedName("posted_by")
    private String posted_by;

    @SerializedName("posted_on")
    private long posted_on;

    @SerializedName("expires_on")
    private long expires_on;

    @SerializedName("poll_question")
    private String poll_question;

    @SerializedName("poll_options")
    private ArrayList<Options_> optionsArrayList;

    @SerializedName("total_count")
    private int total_count;

    @SerializedName("poll_answer")
    private long poll_answer;

    @SerializedName("total_comments")
    private int total_comments;

    @SerializedName("total_likes")
    private int total_likes;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName(General.IS_LIKE)
    private int is_like;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(long id) {
        this.id = id;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public void setPosted_on(long posted_on) {
        this.posted_on = posted_on;
    }

    public void setExpires_on(long expires_on) {
        this.expires_on = expires_on;
    }

    public void setPoll_question(String poll_question) {
        this.poll_question = poll_question;
    }

    public void setPollOptions(ArrayList<Options_> optionsArrayList) {
        this.optionsArrayList = optionsArrayList;
    }

    public void setPoll_answer(long poll_answer) {
        this.poll_answer = poll_answer;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public void setTotal_comments(int total_comments) {
        this.total_comments = total_comments;
    }

    public void setTotal_likes(int total_likes) {
        this.total_likes = total_likes;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setIs_like(int is_like) {
        this.is_like = is_like;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public long getPosted_on() {
        return posted_on;
    }

    public long getExpires_on() {
        return expires_on;
    }

    public String getPoll_question() {
        return poll_question;
    }

    public ArrayList<Options_> getPollOptions() {
        return optionsArrayList;
    }

    public int getTotal_count() {
        return total_count;
    }

    public long getPoll_answer() {
        return poll_answer;
    }

    public int getTotal_comments() {
        return total_comments;
    }

    public int getTotal_likes() {
        return total_likes;
    }

    public String getPhoto() {
        return photo;
    }

    public int getIs_like() {
        return is_like;
    }

    public int getStatus() {
        return status;
    }
}
