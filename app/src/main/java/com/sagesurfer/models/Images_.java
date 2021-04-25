package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 08-08-2017
 * Last Modified on 14-12-2017
 */

public class Images_ implements Serializable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName("thumb_path")
    private String thumb_path;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName("full_path")
    private String full_path;

    @SerializedName(General.MESSAGE)
    private String message;

    @SerializedName(General.COUNT)
    private int count;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("is_like")
    private int is_like;

    @SerializedName("total_like")
    private int total_like;

    @SerializedName("is_cover")
    private int is_cover;

    @SerializedName("selectImgs")
    private boolean selectImgs;

    @SerializedName(General.COMMENT)
    private String comment;

    @SerializedName("username")
    private String username;

    @SerializedName("comment_by")
    private String comment_by;

    @SerializedName("msg")
    private String msg;

    @SerializedName("comment_on")
    private long comment_on;

    @SerializedName("comment_by_userid")
    private long comment_by_userid;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    @SerializedName("added_by")
    private long added_by;

    @SerializedName("added_by_id")
    private long added_by_id;

    @SerializedName("is_modarator")
    private int is_modarator;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbPath(String thumb_path) {
        this.thumb_path = thumb_path;
    }

    public void setFullPath(String full_path) {
        this.full_path = full_path;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*Getter Methods*/

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbPath() {
        return thumb_path;
    }

    public String getFullPath() {
        return full_path;
    }

    public int getStatus() {
        return status;
    }

    public boolean isSelectImgs() {
        return selectImgs;
    }

    public void setSelectImgs(boolean selectImgs) {
        this.selectImgs = selectImgs;
    }

    public int getIs_like() {
        return is_like;
    }

    public void setIs_like(int is_like) {
        this.is_like = is_like;
    }

    public int getTotal_like() {
        return total_like;
    }

    public void setTotal_like(int total_like) {
        this.total_like = total_like;
    }

    public int getIs_cover() {
        return is_cover;
    }

    public void setIs_cover(int is_cover) {
        this.is_cover = is_cover;
    }

    public String getThumb_path() {
        return thumb_path;
    }

    public void setThumb_path(String thumb_path) {
        this.thumb_path = thumb_path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFull_path() {
        return full_path;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_by() {
        return comment_by;
    }

    public void setComment_by(String comment_by) {
        this.comment_by = comment_by;
    }

    public long getComment_on() {
        return comment_on;
    }

    public void setComment_on(long comment_on) {
        this.comment_on = comment_on;
    }

    public long getComment_by_userid() {
        return comment_by_userid;
    }

    public void setComment_by_userid(long comment_by_userid) {
        this.comment_by_userid = comment_by_userid;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getAdded_by() {
        return added_by;
    }

    public void setAdded_by(long added_by) {
        this.added_by = added_by;
    }

    public int getIs_modarator() {
        return is_modarator;
    }

    public void setIs_modarator(int is_modarator) {
        this.is_modarator = is_modarator;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAdded_by_id() {
        return added_by_id;
    }

    public void setAdded_by_id(long added_by_id) {
        this.added_by_id = added_by_id;
    }
}
