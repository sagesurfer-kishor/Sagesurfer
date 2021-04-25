package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.util.List;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class DrawerMenu_ {

    @SerializedName(General.ID)
    private int id;

    @SerializedName("menu")
    private String menu;

    @SerializedName("submenu")
    private List<HomeMenu_> subMenu;

    private boolean isSelected;

    private int counter;

    public void setId(int id) {
        this.id = id;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setSubMenu(List<HomeMenu_> subMenu) {
        this.subMenu = subMenu;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    /*Getter Methods*/
    public int getId() {
        return this.id;
    }

    public String getMenu() {
        return this.menu;
    }

    public List<HomeMenu_> getSubMenu() {
        return this.subMenu;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public int getCounter() {
        return this.counter;
    }
}
