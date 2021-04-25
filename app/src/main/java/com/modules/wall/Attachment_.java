package com.modules.wall;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 10-07-2017
 *         Last Modified on 10-07-2017
 */

public class Attachment_ implements Serializable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.PATH)
    private String path;

    @SerializedName(General.IMAGE)
    private String image;

    @SerializedName(General.SIZE)
    private String size;

    private boolean isPost;

    private boolean isNewFile=false;

    @SerializedName("position")
    private int position;

    public void setPath(String path) {
        this.path = path;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPost(boolean isPost) {
        this.isPost = isPost;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isPost() {
        return isPost;
    }

    public int getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public String getImage() {
        return this.image;
    }

    public String getSize() {
        return this.size;
    }

    public int getPosition() {
        return position;
    }

    public boolean isNewFile() {
        return isNewFile;
    }

    public void setNewFile(boolean newFile) {
        isNewFile = newFile;
    }
}
