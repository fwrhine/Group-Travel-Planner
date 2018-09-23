package com.example.pplki18.grouptravelplanner;

public class Friend {

    String friend_name;
    byte[] friend_image;

    //constructor
    public Friend() {
    }

    public Friend(String friend_name, byte[] friend_image) {
        this.friend_name = friend_name;
        this.friend_image = friend_image;
    }

    //setters
    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public void setFriend_image(byte[] friend_image) {
        this.friend_image = friend_image;
    }

    //getters
    public String getFriend_name() {
        return friend_name;
    }
    public byte[] getFriend_image() {
        return friend_image;
    }

}
