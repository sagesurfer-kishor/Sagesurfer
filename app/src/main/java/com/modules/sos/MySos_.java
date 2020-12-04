package com.modules.sos;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 17-07-2017
 *         Last Modified on 14-12-2017
 */

public class MySos_ {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.MSG)
    private String message;

    @SerializedName("g_id")
    private int group_id;

    @SerializedName(General.TIMESTAMP)
    private long time;

    @SerializedName(General.DEVICE)
    private String device;

    @SerializedName("e1")
    private SosStatus_ e1;

    @SerializedName("e2")
    private SosStatus_ e2;

    @SerializedName("e3")
    private SosStatus_ e3;

    @SerializedName("cc")
    private SosStatus_ cc;

    @SerializedName(General.TYPE)
    private int type;

    @SerializedName(General.CONSUMER_ID)
    private int consumer_id;

    @SerializedName(General.CONSUMER_NAME)
    private String consumer_name;

    @SerializedName(General.CONSUMER_PHOTO)
    private String consumer_photo;

    @SerializedName(General.GROUP_NAME)
    private String group_name;

    @SerializedName(General.STATUS)
    private int status;

    /* Setter Method */

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setGroupId(int group_id) {
        this.group_id = group_id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setE1(SosStatus_ e1) {
        this.e1 = e1;
    }

    public void setE2(SosStatus_ e2) {
        this.e2 = e2;
    }

    public void setE3(SosStatus_ e3) {
        this.e3 = e3;
    }

    public void setCc(SosStatus_ cc) {
        this.cc = cc;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /* Getter Methods*/

    public int getId() {
        return this.id;
    }

    public String getMessage() {
        return this.message;
    }

    public int getGroupId() {
        return this.group_id;
    }

    public long getTime() {
        return this.time;
    }

    public String getDevice() {
        return this.device;
    }

    public SosStatus_ getE1() {
        return this.e1;
    }

    public SosStatus_ getE2() {
        return this.e2;
    }

    public SosStatus_ getE3() {
        return this.e3;
    }

    public SosStatus_ getCc() {
        return this.cc;
    }


    public int getConsumer_id() {
        return consumer_id;
    }

    public void setConsumer_id(int consumer_id) {
        this.consumer_id = consumer_id;
    }

    public String getConsumer_name() {
        return consumer_name;
    }

    public void setConsumer_name(String consumer_name) {
        this.consumer_name = consumer_name;
    }

    public String getConsumer_photo() {
        return consumer_photo;
    }

    public void setConsumer_photo(String consumer_photo) {
        this.consumer_photo = consumer_photo;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getStatus() {
        return this.status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
