package com.example.pplki18.grouptravelplanner.utils;

import java.util.List;

public class Group {

    String group_name;
    byte[] group_image;
    List<String> group_members;
    List<byte[]> group_memberPics;

    //constructor
    public Group() {
    }
    
    public Group(String group_name, byte[] group_image) {
        this.group_name = group_name;
        this.group_image = group_image;
    }

    //setters
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public void setGroup_image(byte[] group_image) {
        this.group_image = group_image;
    }

    public void setGroup_members(List<String> group_members) {
        this.group_members = group_members;
    }

    public void setGroup_memberPics(List<byte[]> group_memberPics) {
        this.group_memberPics = group_memberPics;
    }

    //getters
    public String getGroup_name() {
        return group_name;
    }

    public byte[] getGroup_image() {
        return group_image;
    }

    public List<String> getGroup_members() { return group_members; }

    public List<byte[]> getGroup_memberPics() {
        return group_memberPics;
    }
}
