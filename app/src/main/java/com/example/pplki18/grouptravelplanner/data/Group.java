package com.example.pplki18.grouptravelplanner.data;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String group_name;
    public String group_image_url;
    public String creator_id;
    public List<String> members;

    public Group(){}

    public Group(String group_name, String group_image_url, String creator_id){
        this.group_name = group_name;
        this.group_image_url = group_image_url;
        this.creator_id = creator_id;
        this.members = new ArrayList<>();
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_image_url() {
        return group_image_url;
    }

    public void setGroup_image_url(String group_image_url) {
        this.group_image_url = group_image_url;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public List<String> getMembers() {
        return members;
    }

    public void addMember(String member_id) {
        this.members.add(member_id);
    }
}
