package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 2/26/2019.
 */

//"id":"8131","title":"sagar padmale","full_name":"Sagar3 Padmale3","last_updated":1550927542,"is_delete":0,"status":1
public class MessageBoard_ implements Serializable {
    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.TITLE)
    private String title;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName(General.LAST_UPDATED)
    private long last_updated;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.IS_DELETE)
    private int is_delete;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.STATUS)
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
