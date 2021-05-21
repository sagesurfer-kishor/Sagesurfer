package screen.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetAddNewMember implements Serializable {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("comet_chat_id")
    private String comet_chat_id;

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("photo")
    private String photo;

    public String getComet_chat_id() {
        return comet_chat_id;
    }

    public void setComet_chat_id(String comet_chat_id) {
        this.comet_chat_id = comet_chat_id;
    }

    @SerializedName("is_friend")
    private String is_friend;

    public String getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(String is_friend) {
        this.is_friend = is_friend;
    }

    @SerializedName("status")
    private Integer status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
