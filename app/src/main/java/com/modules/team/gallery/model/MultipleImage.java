package com.modules.team.gallery.model;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;


/**
 * Created by Kailash Karankal on 7/30/2019.
 */
public class MultipleImage {
    @SerializedName(General.ID)
    private long id;

    @SerializedName("file")
    private String file;

    @SerializedName("msg")
    private String msg;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("selectImgs")
    private boolean selectImgs;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @SerializedName("position")
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSelectImgs() {
        return selectImgs;
    }

    public void setSelectImgs(boolean selectImgs) {
        this.selectImgs = selectImgs;
    }
}
