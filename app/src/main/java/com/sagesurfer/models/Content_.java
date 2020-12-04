package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/11/2018
 *         Last Modified on 4/11/2018
 */

public class Content_ implements Serializable {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.CATEGORY)
    private String category;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.TYPE)
    private String type;

    @SerializedName("thumb_path")
    private String thumb_path;

    @SerializedName("tags")
    private String tags;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName("content")
    private String content;

    @SerializedName(General.COMMENT)
    private long comments;

    @SerializedName(General.LIKE)
    private long like;

    @SerializedName(General.IS_LIKE)
    private int is_like;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.SIZE)
    private String size;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.SHARED_TO)
    private String shared_to;

    @SerializedName(General.SHARED_TO_IDS)
    private String shared_to_ids;

    /* Setter Method */

    public void setId(long id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setThumb_path(String thumb_path) {
        this.thumb_path = thumb_path;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public void setIs_like(int is_like) {
        this.is_like = is_like;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSize(String size) {
        this.size = size;
    }

    /* Getter Methods */

    public long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getThumb_path() {
        return thumb_path;
    }

    public String getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public long getComments() {
        return comments;
    }

    public long getLike() {
        return like;
    }

    public int getIs_like() {
        return is_like;
    }

    public int getStatus() {
        return status;
    }

    public String getSize() {
        return size;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getShared_to() {
        return shared_to;
    }

    public void setShared_to(String shared_to) {
        this.shared_to = shared_to;
    }

    public String getShared_to_ids() {
        return shared_to_ids;
    }

    public void setShared_to_ids(String shared_to_ids) {
        this.shared_to_ids = shared_to_ids;
    }
}
