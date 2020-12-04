package com.modules.selfcare;

import com.sagesurfer.models.City_;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Girish Mane <girish@sagesurfer.com>
 *         Created on 18-10-2016
 *         Last Modified on 18-10-2016
 */

public class ListItem {

    private String name;
    private String id;
    private boolean selected;
    private String photo;
    private int status;
    private ArrayList<ListItem> cityList;

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean getSelected() {
        return selected;
    }


    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<ListItem> getCityList() {
        return cityList;
    }

    public void setCityList(ArrayList<ListItem> cityList) {
        this.cityList = cityList;
    }
}


