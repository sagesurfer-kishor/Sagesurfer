package com.modules.wall;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 10-07-2017
 *         Last Modified on 10-07-2017
 */

public class Feed_ {

    @SerializedName("wall_id")
    private int id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.PROFILE_PHOTO)
    private String profile_photo;

    @SerializedName("date_of_added")
    private long date;

    @SerializedName("feed")
    private String feed;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName(General.LIKE_COUNT)
    private int like_count;

    @SerializedName(General.COMMENT_COUNT)
    private int comment_count;

    @SerializedName(General.SHARE_COUNT)
    private int share_count;

    @SerializedName(General.IS_LIKE)
    private int is_like;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("totalUploads")
    private int total_upload;


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @SerializedName(General.ATTACHMENTS)
    private ArrayList<Attachment_> attachmentList;

    public int getShare_count() {
        return share_count;
    }

    public void setShare_count(int share_count) {
        this.share_count = share_count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePhoto(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public void setLikeCount(int like_count) {
        this.like_count = like_count;
    }

    public void setCommentCount(int comment_count) {
        this.comment_count = comment_count;
    }

    public void setIsLike(int is_like) {
        this.is_like = is_like;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTotalUpload(int total_upload) {
        this.total_upload = total_upload;
    }

    public void setAttachmentList(ArrayList<Attachment_> attachmentList) {
        this.attachmentList = attachmentList;
    }
    /*Getter Method*/

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getProfilePhoto() {
        return this.profile_photo;
    }

    public long getDate() {
        return this.date;
    }

    public String getFeed() {
        return this.feed;
    }

    public int getLikeCount() {
        return this.like_count;
    }

    public int getCommentCount() {
        return this.comment_count;
    }

    public int getIsLike() {
        return this.is_like;
    }

    public int getStatus() {
        return this.status;
    }

    public int getTotalUpload() {
        return this.total_upload;
    }

    public ArrayList<Attachment_> getAttachmentList() {
        return this.attachmentList;
    }
}
