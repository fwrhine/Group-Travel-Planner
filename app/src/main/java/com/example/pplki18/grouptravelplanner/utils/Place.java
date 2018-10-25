package com.example.pplki18.grouptravelplanner.utils;

import java.util.List;

public class Place {

    private String place_id;
    private String name;
    private String address;
    private String rating;
    private String phone_number;
//    String opening_hours;
    private String website;
    private String photo;
    private String url;
    private Boolean open_now;
    private List<String> weekday_text;


    public Place() {

    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setOpen_now(Boolean open_now) {
        this.open_now = open_now;
    }

    public void setWeekday_text(List<String> weekday_text) {
        this.weekday_text = weekday_text;
    }



    public String getPlace_id() {
        return place_id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getRating() {
        return rating;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getOpen_now() {
        return open_now;
    }

    public List<String> getWeekday_text() {
        return weekday_text;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
