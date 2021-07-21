package com.modules.wall;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 10-07-2017
 *         Last Modified on 10-07-2017
 */

public class Feed_ implements Parcelable {
    @SerializedName("wall_id")
    private int wall_id;

    @SerializedName(General.NAME)
    private String name;

    @SerializedName(General.PROFILE_PHOTO)
    private String profile_photo;

    @SerializedName("date_of_added")
    private long date;

    @SerializedName("feed")
    private String feed;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName(General.LIKE_COUNT)
    private int like_count;

    @SerializedName(General.COMMENT_COUNT)
    private int comment_count;

    @SerializedName(General.SHARE_COUNT)
    private int share_count;

    @SerializedName(General.IS_LIKE)
    private int is_like;

    @SerializedName(General.STATUS)
    private int status;

    @SerializedName("totalUploads")
    private int total_upload;

    public Feed_ (int status){
        this.status=status;
    }

    protected Feed_(Parcel in) {
        wall_id = in.readInt();
        name = in.readString();
        profile_photo = in.readString();
        date = in.readLong();
        feed = in.readString();
        user_id = in.readInt();
        like_count = in.readInt();
        comment_count = in.readInt();
        share_count = in.readInt();
        is_like = in.readInt();
        status = in.readInt();
        total_upload = in.readInt();
    }

    public static final Creator<Feed_> CREATOR = new Creator<Feed_>() {
        @Override
        public Feed_ createFromParcel(Parcel in) {
            return new Feed_(in);
        }

        @Override
        public Feed_[] newArray(int size) {
            return new Feed_[size];
        }
    };

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @SerializedName(General.ATTACHMENTS)
    private ArrayList<Attachment_> attachmentList;

    public int getShare_count() {
        return share_count;
    }

    public void setShare_count(int share_count) {
        this.share_count = share_count;
    }

    public void setWall_id(int wall_id) {
        this.wall_id = wall_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePhoto(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public void setLikeCount(int like_count) {
        this.like_count = like_count;
    }

    public void setCommentCount(int comment_count) {
        this.comment_count = comment_count;
    }

    public void setIsLike(int is_like) {
        this.is_like = is_like;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTotalUpload(int total_upload) {
        this.total_upload = total_upload;
    }

    public void setAttachmentList(ArrayList<Attachment_> attachmentList) {
        this.attachmentList = attachmentList;
    }
    /*Getter Method*/

    public int getWall_id() {
        return this.wall_id;
    }

    public String getName() {
        return this.name;
    }

    public String getProfilePhoto() {
        return this.profile_photo;
    }

    public long getDate() {
        return this.date;
    }

    public String getFeed() {
        return this.feed;
    }

    public int getLikeCount() {
        return this.like_count;
    }

    public int getCommentCount() {
        return this.comment_count;
    }

    public int getIsLike() {
        return this.is_like;
    }

    public int getStatus() {
        return this.status;
    }

    public int getTotalUpload() {
        return this.total_upload;
    }

    public ArrayList<Attachment_> getAttachmentList() {
        return this.attachmentList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(wall_id);
        parcel.writeString(name);
        parcel.writeString(profile_photo);
        parcel.writeLong(date);
        parcel.writeString(feed);
        parcel.writeInt(user_id);
        parcel.writeInt(like_count);
        parcel.writeInt(comment_count);
        parcel.writeInt(share_count);
        parcel.writeInt(is_like);
        parcel.writeInt(status);
        parcel.writeInt(total_upload);
    }
}
