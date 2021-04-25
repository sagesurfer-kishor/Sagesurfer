package com.modules.fms;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * Created by GirishM on 01-08-2017.
 */

public class Folder_ {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("total_files")
    private int total_files;

    @SerializedName("total_folders")
    private int total_folders;

    @SerializedName(General.STATUS)
    private int status;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalFiles(int total_files) {
        this.total_files = total_files;
    }

    public void setTotalFolders(int total_folders) {
        this.total_folders = total_folders;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*** Getter Methods ***/

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalFiles() {
        return total_files;
    }

    public int getTotalFolders() {
        return total_folders;
    }

    public int getStatus() {
        return status;
    }
}
