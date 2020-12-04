package com.modules.supervisor;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 29-07-2017
 * Last Modified on 14-12-2017
 */

public class Message_ {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName("send_date")
    private long send_date;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.USER_ID)
    private int user_id;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(long send_date) {
        this.send_date = send_date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setIsRead(int is_read) {
        this.is_read = is_read;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getDate() {
        return send_date;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getImage() {
        return image;
    }

    public int getUserId() {
        return user_id;
    }

    public int getIsRead() {
        return is_read;
    }

    public int getStatus() {
        return status;
    }
}
