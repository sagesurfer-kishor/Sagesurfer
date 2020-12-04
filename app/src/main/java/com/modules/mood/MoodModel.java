package com.modules.mood;import com.google.gson.annotations.SerializedName;import com.sagesurfer.constant.General;import java.io.Serializable;public class MoodModel implements Serializable {    @SerializedName(General.ID)    private int id;    @SerializedName(General.URL)    private String url;    @SerializedName(General.NAME)    private String name;    @SerializedName(General.COLOR)    private String colorName;    @SerializedName(General.STATUS)    private int status;    @SerializedName(General.IS_INTENSITY)    private int is_intensity;    private boolean isSelected;    public int getId() {        return id;    }    public void setId(int id) {        this.id = id;    }    public String getUrl() {        return url;    }    public void setUrl(String url) {        this.url = url;    }    public String getName() {        return name;    }    public void setName(String name) {        this.name = name;    }    public int getStatus() {        return status;    }    public void setStatus(int status) {        this.status = status;    }    public int getIs_intensity() {        return is_intensity;    }    public void setIs_intensity(int is_intensity) {        this.is_intensity = is_intensity;    }    public String getColorName() {        return colorName;    }    public void setColorName(String colorName) {        this.colorName = colorName;    }    public boolean isSelected() {        return isSelected;    }    public void setSelected(boolean selected) {        isSelected = selected;    }}