package com.example.android.lapitchat;

public class Users {
    public String name;
    public String image;
    public String status;
    //public String online;




    public Users(){

    }
    public Users(String name, String image, String status,String online) {
        this.name = name;
        this.image = image;
        this.status = status;
        //this.online=online;
    }
//    public String isOnline() {
//        return online;
//    }
//
//    public void setOnline(String online) {
//        this.online = online;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
