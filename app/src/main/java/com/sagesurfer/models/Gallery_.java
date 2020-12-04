package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

public class Gallery_ {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.FULL_NAME)
    private String full_name;

    @SerializedName(General.MESSAGE)
    private String message;

    @SerializedName(General.COUNT)
    private int count;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("selectImgs")
    private boolean selectImgs;

    @SerializedName("msg")
    private String msg;

    @SerializedName("added_by")
    private long added_by;

    @SerializedName("is_modarator")
    private int is_modarator;

    @SerializedName("is_cc")
    private int is_cc;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    /* Getter Method */

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getFullName() {
        return full_name;
    }

    public int getCount() {
        return count;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
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

    public int getIs_cc() {
        return is_cc;
    }

    public void setIs_cc(int is_cc) {
        this.is_cc = is_cc;
    }
}
