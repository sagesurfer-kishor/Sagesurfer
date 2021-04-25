package com.modules.motivation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

/**
 * Created by Kailash Karankal on 6/21/2019.
 */
public class ToolKitData implements Parcelable {

    @SerializedName(General.ID)
    private int id;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName(General.TIMESTAMP)
    private Long timestamp;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName("datetime")
    private String datetime;

    @SerializedName("toolkit_ids")
    private String toolkit_ids;

    @SerializedName("msg")
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private boolean selected = false;


    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getToolkit_ids() {
        return toolkit_ids;
    }

    public void setToolkit_ids(String toolkit_ids) {
        this.toolkit_ids = toolkit_ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * Constructs a Question from values
     */
    public ToolKitData(int id, int status, Long timestamp, String name, String toolkit_ids, String msg, boolean selected) {
        this.id = id;
        this.status = status;
        // this.timestamp = timestamp;
        this.name = name;
        this.toolkit_ids = toolkit_ids;
        this.msg = msg;
        this.selected = selected;
    }

    public ToolKitData() {
    }

    /**
     * Constructs a Question from a Parcel
     *
     * @param parcel Source Parcel
     */
    public ToolKitData(Parcel parcel) {
        this.id = parcel.readInt();
        this.status = parcel.readInt();
        // this.timestamp = parcel.readLong();
        this.name = parcel.readString();
        this.toolkit_ids = parcel.readString();
        this.msg = parcel.readString();
        this.selected = parcel.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Required method to write to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(status);
        //  dest.writeLong(timestamp);
        dest.writeString(name);
        dest.writeString(toolkit_ids);
        dest.writeString(msg);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    // Method to recreate a Question from a Parcel
    public static Creator<ToolKitData> CREATOR = new Creator<ToolKitData>() {

        @Override
        public ToolKitData createFromParcel(Parcel source) {
            return new ToolKitData(source);
        }

        @Override
        public ToolKitData[] newArray(int size) {
            return new ToolKitData[size];
        }

    };
}
