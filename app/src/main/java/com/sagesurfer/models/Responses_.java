package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 21-07-2017
 *         Last Modified on 21-07-2017
 */

public class Responses_ {

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.MSG)
    private String message;

    @SerializedName(General.ERROR)
    private String error;

    @SerializedName("invalid_username")
    private String invalid_username;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setUsername(String invalid_username) {
        this.invalid_username = invalid_username;
    }

    /*Getter method*/
    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public String getError() {
        return this.error;
    }

    public String getUsername() {
        return this.invalid_username;
    }
}
