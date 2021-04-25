package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class Server_ implements Serializable {

    @SerializedName(General.ID)
    private String id;

    @SerializedName(General.INSTANCE_KEY)
    private String key;

    @SerializedName("instance_url")
    private String domain_url;

    @SerializedName("cometchat")
    private String chat_url;

    @SerializedName(General.STATUS)
    private int status;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDomainUrl(String domain_url) {
        this.domain_url = domain_url;
    }

    public void setChatUrl(String chat_url) {
        this.chat_url = chat_url;
    }

    public int getStatus() {
        return this.status;
    }

    public String getId() {
        return this.id;
    }

    public String getDomainUrl() {
        return this.domain_url;
    }

    public String getChatUrl() {
        return this.chat_url;
    }

    public String getKey() {
        return this.key;
    }
}
