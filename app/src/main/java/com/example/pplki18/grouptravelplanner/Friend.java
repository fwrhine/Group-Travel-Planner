package com.example.pplki18.grouptravelplanner;

public class Friend {

    String friend_username;
    byte[] friend_image;
    //constructor
    public Friend() {
    }

    public Friend(String friend_username, byte[] friend_image) {
        this.friend_username = friend_username;
        this.friend_image = friend_image;
    }

    //setters
    public void setFriend_username(String friend_username) {
        this.friend_username = friend_username;
    }
    public void setUser_friend_image(byte[] friend_image) {
        this.friend_image = friend_image;
    }

    //getters

    public String getFriend_username() {
        return friend_username;
    }
    public byte[] getFriend_image() {
        return friend_image;
    }

}
