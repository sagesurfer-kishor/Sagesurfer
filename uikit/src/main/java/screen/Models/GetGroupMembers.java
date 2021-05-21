package screen.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetGroupMembers implements Serializable {
    @SerializedName("user_id")

    private String userId;
    @SerializedName("name")

    private String name;
    @SerializedName("username")

    private String username;
    @SerializedName("photo")

    private String photo;

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

