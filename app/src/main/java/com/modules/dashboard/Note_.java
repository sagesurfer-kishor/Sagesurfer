package com.modules.dashboard;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 04-10-2017
 *         Last Modified on 16-11-2017
 */

public class Note_ {

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.MESSAGE)
    private String message;

    @SerializedName("_left")
    private int _left;

    @SerializedName("_top")
    private int _top;

    @SerializedName("color")
    private int color;

    @SerializedName("created_date")
    private String created_date;

    @SerializedName("share_by_count")
    private long share_by_count;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void set_left(int _left) {
        this._left = _left;
    }

    public void set_top(int _top) {
        this._top = _top;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public void setShare_by_count(long share_by_count) {
        this.share_by_count = share_by_count;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int get_left() {
        return _left;
    }

    public int get_top() {
        return _top;
    }

    public String getCreated_date() {
        return created_date;
    }

    public int getColor() {
        return color;
    }

    public long getShare_by_count() {
        return share_by_count;
    }

    public int getStatus() {
        return status;
    }
}
