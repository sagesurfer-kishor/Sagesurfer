package com.modules.fms;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author girish M (girish@sagesurfer.com)
 *         Created on 12/09/17.
 *         Last Modified on 06/06/17.
 */

public class AllFolder_ implements Serializable{

    @SerializedName(General.ID)
    private long id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("sub_folder")
    private ArrayList<Folder_> sub_folder;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSub_folder(ArrayList<Folder_> sub_folder) {
        this.sub_folder = sub_folder;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Folder_> getSub_folder() {
        return sub_folder;
    }

    public int getStatus() {
        return status;
    }
}
