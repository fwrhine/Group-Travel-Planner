package com.example.pplki18.grouptravelplanner.data;

import java.util.ArrayList;
import java.util.List;

public class User {

    public String id;
    public String fullName;
    public String username;
    public String email;
    public String gender;
    public String phone;
    public String birthday;
    public String photoUrl;
    public String status;
    public List<String> friends;

    public User(){}

    public User(String id, String fullName, String username, String email){
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.gender = "0";
        this.phone = "0";
        this.birthday = "none";
        this.photoUrl = "none";
        this.status = "0";
        this.friends = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
