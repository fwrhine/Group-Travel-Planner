package com.example.pplki18.grouptravelplanner.utils;

public class Place {

    String place_id;
    String name;
    String address;
    int rating;
    String phone_number;
//    String opening_hours;
    String website;
//    String photo;

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

    public void setRating(int rating) {
        this.rating = rating;
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

    public int getRating() {
        return rating;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhone_number() {
        return phone_number;
    }
}
