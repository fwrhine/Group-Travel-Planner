package com.example.pplki18.grouptravelplanner.utils;

public class User {

    String user_name;
    byte[] user_image;

    //constructor
    public User() {
    }

    public User(String user_name, byte[] user_image) {
        this.user_name = user_name;
        this.user_image = user_image;


    }

    //setter
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_image(byte[] user_image) { this.user_image = user_image; }


    //getter
    public String getUser_name() {
        return user_name;
    }

    public byte[] getUser_image() {
        return user_image;
    }
}
