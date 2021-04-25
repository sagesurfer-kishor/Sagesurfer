package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 9/7/2018.
 */

public class CometChatTeamMembers_ implements Serializable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.N)
    private String n;

    @SerializedName(General.L)
    private String l;

    @SerializedName(General.A) //image
    private String a;

    @SerializedName(General.D)
    private int d;

    @SerializedName(General.S)
    private String s;

    @SerializedName(General.M)
    private String m;

    @SerializedName(General.LS)
    private long ls;

    @SerializedName(General.LSTN)
    private int lstn;

    @SerializedName(General.CH)
    private String ch;

    @SerializedName(General.RDRS)
    private int rdrs;

    @SerializedName(General.PROTYPE)
    private int protype;

    @SerializedName(General.GID)
    private int gid;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.FIRST_NAME)
    private String firstname;

    @SerializedName(General.ROLE)
    private String role;

    @SerializedName(General.TOTAL_UNREAD_MESSAGES)
    private String unread_message_count;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public long getLs() {
        return ls;
    }

    public void setLs(long ls) {
        this.ls = ls;
    }

    public int getLstn() {
        return lstn;
    }

    public void setLstn(int lstn) {
        this.lstn = lstn;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public int getRdrs() {
        return rdrs;
    }

    public void setRdrs(int rdrs) {
        this.rdrs = rdrs;
    }

    public int getProtype() {
        return protype;
    }

    public void setProtype(int protype) {
        this.protype = protype;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
