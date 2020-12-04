package com.modules.admin;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 22-08-2017
 * Last Modified on 16-11-2017
 */

public class SpamItem_ {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.TYPE)
    private String type;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName("team")
    private String team;

    @SerializedName(General.CREATED_BY)
    private String created_by;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName(General.TIMESTAMP)
    private long timestamp;

    @SerializedName(General.STATUS)
    private int status;

    /* Setter Methods */

    public void setId(long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Methods */

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTeam() {
        return team;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getPhoto() {
        return photo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }
}
