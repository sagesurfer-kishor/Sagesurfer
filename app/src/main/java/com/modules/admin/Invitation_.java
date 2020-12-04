package com.modules.admin;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 23-08-2017
 * Last Modified on 13-12-2017
 */


public class Invitation_ implements Serializable {

    @SerializedName("request_send")
    private String request_send;

    @SerializedName("send_profile")
    private String send_profile;

    @SerializedName("is_auth")
    private int is_auth;

    @SerializedName("auth_circle")
    private String auth_circle;

    @SerializedName("auth_status")
    private String auth_status;

    @SerializedName("auth_user")
    private String auth_user;

    @SerializedName("auth_profile")
    private String auth_profile;

    @SerializedName("receiver_status")
    private String receiver_status;

    @SerializedName("receiver_user")
    private String receiver_user;

    @SerializedName("receiver_circle")
    private String receiver_circle;

    @SerializedName("receiver_profile")
    private String receiver_profile;

    @SerializedName("receiver_email")
    private String receiver_email;

    @SerializedName("is_popup")
    private int is_popup;

    @SerializedName("is_checkbox")
    private int is_checkbox;

    @SerializedName("varmsg")
    private String varmsg;

    @SerializedName(General.ID)
    private long id;

    @SerializedName("uid")
    private long u_id;

    @SerializedName(General.TEAM)
    private String team;

    @SerializedName(General.ACTION)
    private String action;

    @SerializedName(General.TYPE)
    private String type;

    @SerializedName(General.STATUS)
    private int status;

    /* Setter Methods */

    public void setId(long id) {
        this.id = id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public void setRequest_send(String request_send) {
        this.request_send = request_send;
    }

    public void setSend_profile(String send_profile) {
        this.send_profile = send_profile;
    }

    public void setIs_auth(int is_auth) {
        this.is_auth = is_auth;
    }

    public void setAuth_circle(String auth_circle) {
        this.auth_circle = auth_circle;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public void setAuth_user(String auth_user) {
        this.auth_user = auth_user;
    }

    public void setAuth_profile(String auth_profile) {
        this.auth_profile = auth_profile;
    }

    public void setReceiver_status(String receiver_status) {
        this.receiver_status = receiver_status;
    }

    public void setReceiver_user(String receiver_user) {
        this.receiver_user = receiver_user;
    }

    public void setReceiver_circle(String receiver_circle) {
        this.receiver_circle = receiver_circle;
    }

    public void setReceiver_profile(String receiver_profile) {
        this.receiver_profile = receiver_profile;
    }

    public void setIs_popup(int is_popup) {
        this.is_popup = is_popup;
    }

    public void setIs_checkbox(int is_checkbox) {
        this.is_checkbox = is_checkbox;
    }

    public void setVarmsg(String varmsg) {
        this.varmsg = varmsg;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReceiver_email(String receiver_email) {
        this.receiver_email = receiver_email;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Methods */

    public int getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public String getTeam() {
        return team;
    }

    public String getVarmsg() {
        return varmsg;
    }

    public int getIs_checkbox() {
        return is_checkbox;
    }

    public int getIs_popup() {
        return is_popup;
    }

    public String getReceiver_profile() {
        return receiver_profile;
    }

    public String getReceiver_circle() {
        return receiver_circle;
    }

    public String getReceiver_user() {
        return receiver_user;
    }

    public String getReceiver_status() {
        return receiver_status;
    }

    public String getAuth_profile() {
        return auth_profile;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public String getAuth_circle() {
        return auth_circle;
    }

    public String getAuth_user() {
        return auth_user;
    }

    public int getIs_auth() {
        return is_auth;
    }

    public String getReceiver_email() {
        return receiver_email;
    }

    public String getSend_profile() {
        return send_profile;
    }

    public String getRequest_send() {
        return request_send;
    }

    public long getId() {
        return id;
    }

    long getU_id() {
        return u_id;
    }
}
