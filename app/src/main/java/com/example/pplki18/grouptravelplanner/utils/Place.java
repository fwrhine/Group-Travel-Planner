package com.example.pplki18.grouptravelplanner.utils;

public class Place {

    String place_id;
    String name;
    String address;
    int rating;

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
}
