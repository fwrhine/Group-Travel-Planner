package com.example.pplki18.grouptravelplanner;

public class Friend {

    Integer friend_id;
    Integer user_id;
    String friend_name;
    //constructor
    public Friend() {
    }

    public Friend(Integer user_id, Integer friend_id, String friend_name) {
        this.user_id =user_id;
        this.friend_id = friend_id;
        this.friend_name = friend_name;
    }

    //setters
    public void setFriend_id(Integer friend_id) {
        this.friend_id = friend_id;
    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    //getters
    public Integer getFriend_id() {
        return friend_id;
    }
    public Integer getUser_id() {
        return user_id;
    }
    public String getFriend_Name() {
        return friend_name;
    }

}
