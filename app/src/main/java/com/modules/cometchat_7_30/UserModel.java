package com.modules.cometchat_7_30;

public class UserModel {
    String photo;
    String firstname;
    String lastname;
    String From_time;
    String To_time;
    String status;

    public UserModel(String photo, String firstname, String lastname, String from_time, String to_time, String status) {
        this.photo = photo;
        this.firstname = firstname;
        this.lastname = lastname;
        From_time = from_time;
        To_time = to_time;
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFrom_time() {
        return From_time;
    }

    public void setFrom_time(String from_time) {
        From_time = from_time;
    }

    public String getTo_time() {
        return To_time;
    }

    public void setTo_time(String to_time) {
        To_time = to_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
