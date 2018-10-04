package com.example.pplki18.grouptravelplanner.utils;

public class Friend {

//    String friend_username;
//    byte[] friend_image;
    Integer id;
    //constructor
    public Friend() {
    }

    public Friend(int id) {
//        this.friend_username = friend_username;
//        this.friend_image = friend_image;
        this.id = id;
    }

    //setters
//    public void setFriend_username(String friend_username) {
//        this.friend_username = friend_username;
//    }
//    public void setUser_friend_image(byte[] friend_image) {
//        this.friend_image = friend_image;
//    }

    public void setId(Integer i){
        this.id = i;
    }

    //getters
//
//    public String getFriend_username() {
//        return friend_username;
//    }
//    public byte[] getFriend_image() {
//        return friend_image;
    public int getId(){
        return id;
    }
//    }

}
