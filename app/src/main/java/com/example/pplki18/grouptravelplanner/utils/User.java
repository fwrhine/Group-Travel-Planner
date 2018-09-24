package com.example.pplki18.grouptravelplanner.utils;

public class User {

    int user_id;
    String user_name;
    byte[] user_image;

    //constructor
    public User() {
    }

    public User(int user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
//        this.user_image = user_image;


    }

    //setter
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_image(byte[] user_image) { this.user_image = user_image; }


    //getter
    public int getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public byte[] getUser_image() {
        return user_image;
    }
}
