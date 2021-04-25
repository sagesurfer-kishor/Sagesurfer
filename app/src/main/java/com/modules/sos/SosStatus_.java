package com.modules.sos;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * Created by GirishM on 28-07-2017.
 */

public class SosStatus_ {

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.USERNAME)
    private String username;

    /**
     * Setter Methods
     **/
    public void setStatus(int status) {
        this.status = status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Getter Methods
     **/
    public int getStatus() {
        return this.status;
    }

    public String getImage() {
        return this.image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
