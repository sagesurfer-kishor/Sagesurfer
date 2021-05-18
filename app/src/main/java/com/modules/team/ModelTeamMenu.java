package com.modules.team;

import android.graphics.drawable.Drawable;

public class ModelTeamMenu {
    String name;
    Drawable Image_res;

    public ModelTeamMenu(String name, Drawable image_res) {
        this.name = name;
        Image_res = image_res;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage_res() {
        return Image_res;
    }

    public void setImage_res(Drawable image_res) {
        Image_res = image_res;
    }
}
