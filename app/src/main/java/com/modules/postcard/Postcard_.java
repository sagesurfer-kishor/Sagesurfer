package com.modules.postcard;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.PostcardAttachment_;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 18-07-2017
 *         Last Modified on 13-11-2017
 */

public class Postcard_ implements Serializable {

    @SerializedName("msg_id")
    private long message_id;

    @SerializedName(General.ID)
    private long id;

    @SerializedName("subject")
    private String subject;

    @SerializedName(General.DESCRIPTION)
    private String description;

    @SerializedName("sender_id")
    private int sender_id;

    @SerializedName(General.USERNAME)
    private String user_name;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName("folder")
    private String folder;

    @SerializedName("Ttype")
    private String type;

    @SerializedName("send_to_all")
    private String send_to_all;

    @SerializedName("send_cc_all")
    private String send_cc_all;

    @SerializedName("is_to_cc")
    private int is_to_cc;

    @SerializedName("date_of_added")
    private long date;

    @SerializedName(General.IS_READ)
    private int is_read;

    @SerializedName(General.ATTACHMENTS)
    private ArrayList<PostcardAttachment_> attachmentArrayList;

    @SerializedName(General.IS_ATTACHMENT)
    private int is_attachment;

    @SerializedName("is_new")
    private int is_new;

    @SerializedName(General.STATUS)
    private int status;

    /*Setter Methods*/
    public void setMessageId(long message_id) {
        this.message_id = message_id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSenderId(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setToAll(String send_to_all) {
        this.send_to_all = send_to_all;
    }

    public void setCcAll(String send_cc_all) {
        this.send_cc_all = send_cc_all;
    }

    public void setIsToCc(int is_to_cc) {
        this.is_to_cc = is_to_cc;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setIsRead(int is_read) {
        this.is_read = is_read;
    }

    public void setAttachmentArrayList(ArrayList<PostcardAttachment_> attachmentArrayList) {
        this.attachmentArrayList = attachmentArrayList;
    }

    public void setIsAttachment(int is_attachment) {
        this.is_attachment = is_attachment;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setNew(int is_new) {
        this.is_new = is_new;
    }

    /*Getter Methods*/

    public long getMessageId() {
        return this.message_id;
    }

    public long getId() {
        return this.id;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getDescription() {
        return this.description;
    }

    public int getSenderId() {
        return this.sender_id;
    }

    public String getUserName() {
        return this.user_name;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoto() {
        return this.photo;
    }

    public String getFolder() {
        return this.folder;
    }

    public String getType() {
        return this.type;
    }

    public String getToAll() {
        return this.send_to_all;
    }

    public String getCcAll() {
        return this.send_cc_all;
    }

    public int getIsToCc() {
        return this.is_to_cc;
    }

    public long getDate() {
        return this.date;
    }

    public int getIsRead() {
        return this.is_read;
    }

    public ArrayList<PostcardAttachment_> getAttachmentArrayList() {
        return this.attachmentArrayList;
    }

    public int getIsAttachment() {
        return this.is_attachment;
    }

    public int getStatus() {
        return this.status;
    }

    public int getNew() {
        return this.is_new;
    }
}
