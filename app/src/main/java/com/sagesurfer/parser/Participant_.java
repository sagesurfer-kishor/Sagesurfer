package com.sagesurfer.parser;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 18-08-2017
 *         Last Modified on 16-11-2017
 */


public class Participant_ implements Serializable{

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.PHOTO)
    private String photo;

    @SerializedName(General.ROLE)
    private String role;

    @SerializedName("is_accepted")
    private int is_accepted;

    /* Setter Methods */

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setIs_accepted(int is_accepted) {
        this.is_accepted = is_accepted;
    }

    /* Getter Methods */

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getRole() {
        return role;
    }

    public int getIs_accepted() {
        return is_accepted;
    }
}
