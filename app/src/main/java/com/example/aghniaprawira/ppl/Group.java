package com.example.aghniaprawira.ppl;

public class Group {

    String group_name;
    byte[] group_image;

    //constructor
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

    //getters
    public String getGroup_name() {
        return group_name;
    }

    public byte[] getGroup_image() {
        return group_image;
    }

}
