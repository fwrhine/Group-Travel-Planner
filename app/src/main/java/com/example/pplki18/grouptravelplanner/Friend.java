package com.example.pplki18.grouptravelplanner;

public class Friend {

    Integer friend_id;
    Integer user_id;
    //constructor
    public Friend() {
    }

    public Friend(Integer user_id, Integer friend_id) {
        this.user_id =user_id;
        this.friend_id = friend_id;
    }

    //setters
    public void setFriend_id(Integer friend_id) {
        this.friend_id = friend_id;
    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    //getters
    public Integer getFriend_id() {
        return friend_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

}
