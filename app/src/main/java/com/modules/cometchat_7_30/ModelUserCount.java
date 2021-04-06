package com.modules.cometchat_7_30;

public class ModelUserCount {
    String Count;
    String UID;

    public ModelUserCount(String count, String UID) {
        Count = count;
        this.UID = UID;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
