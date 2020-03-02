package com.example.mychat;

public class Users {
    public String userName,status,image;

    public Users(){};
    public Users(String userName, String status, String image) {
        this.userName = userName;
        this.status = status;
        this.image = image;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
