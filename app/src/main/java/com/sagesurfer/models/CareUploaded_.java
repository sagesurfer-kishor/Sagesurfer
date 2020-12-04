package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/11/2018
 *         Last Modified on 4/11/2018
 */

public class CareUploaded_ implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.USER_ID)
    private long user_id;

    @SerializedName(General.USERNAME)
    private String username;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("catagory_id")
    private long category_id;

    @SerializedName("catagory_name")
    private String category_name;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName(General.TYPE)
    private String type;

    @SerializedName("content")
    private String content;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName("tags")
    private String tags;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName("thumb_path")
    private String thumb_path;

    @SerializedName("added_date")
    private long added_date;

    @SerializedName("resubmit_count")
    private int resubmit_count;

    @SerializedName(General.ACTION)
    private int action;

    @SerializedName(General.COMMENT)
    private int comment;

    @SerializedName(General.SIZE)
    private String size;

    @SerializedName(General.SHARED_TO)
    private String shared_to;

    @SerializedName(General.WEBSITE)
    private String website;

    @SerializedName(General.LANGUAGE)
    private String language;

    @SerializedName(General.AGE)
    private String age;

    @SerializedName(General.SHARED_TO_IDS)
    private String shared_to_ids;

    @SerializedName(General.LANGUAGE_ID)
    private long language_id;

    @SerializedName(General.AGE_Id)
    private String age_id;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(long id) {
        this.id = id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCatagory_id(long category_id) {
        this.category_id = category_id;
    }

    public void setCatagory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumb_path(String thumb_path) {
        this.thumb_path = thumb_path;
    }

    public void setAdded_date(long added_date) {
        this.added_date = added_date;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public void setResubmit_count(int resubmit_count) {
        this.resubmit_count = resubmit_count;
    }

    public void setShared_to(String shared_to) {
        this.shared_to = shared_to;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*Getter Methods*/

    public long getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public long getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public String getThumb_path() {
        return thumb_path;
    }

    public long getAdded_date() {
        return added_date;
    }

    public int getResubmit_count() {
        return resubmit_count;
    }

    public String getSize() {
        return size;
    }

    public int getAction() {
        return action;
    }

    public int getComment() {
        return comment;
    }

    public String getShared_to() {
        return shared_to;
    }

    public int getStatus() {
        return status;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getShared_to_ids() {
        return shared_to_ids;
    }

    public void setShared_to_ids(String shared_to_ids) {
        this.shared_to_ids = shared_to_ids;
    }

    public long getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(long language_id) {
        this.language_id = language_id;
    }

    public String getAge_id() {
        return age_id;
    }

    public void setAge_id(String age_id) {
        this.age_id = age_id;
    }
}
