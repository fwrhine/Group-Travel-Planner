package com.example.pplki18.grouptravelplanner.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Group implements Parcelable{

    public String group_id;
    public String group_name;
    public String group_image_url;
    public String creator_id;
    public HashMap<String, Long> read_stamps;
    public List<String> members;

    private static final int MEMBERS_SHOWN = 4;

    public Group(){}

    public Group(String group_id, String group_name, String group_image_url, String creator_id){
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_image_url = group_image_url;
        this.creator_id = creator_id;
        this.members = new ArrayList<>();
        this.read_stamps = new HashMap<>();
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
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

    public List<String> getFirstMembers(){
        return members.subList(0, Math.min(members.size(),MEMBERS_SHOWN));
    }

    public HashMap<String, Long> getRead_stamps() {
        return read_stamps;
    }

    public void setRead_stamps(HashMap<String, Long> read_stamps) {
        this.read_stamps = read_stamps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.group_id);
        parcel.writeString(this.group_name);
        parcel.writeString(this.group_image_url);
        parcel.writeString(this.creator_id);
        parcel.writeList(this.members);
        parcel.writeMap(this.read_stamps);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Group(Parcel in) {
        this.group_id = in.readString();
        this.group_name = in.readString();
        this.group_image_url = in.readString();
        this.creator_id = in.readString();
        this.members = in.readArrayList(null);
        this.read_stamps = in.readHashMap(null);
    }
}
