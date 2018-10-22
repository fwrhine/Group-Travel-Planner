package com.example.pplki18.grouptravelplanner.utils;

import java.util.List;

public class Hotel {
    private String hotel_id;
    private String name;
    private String rating;
    private String region;


    public Hotel() {

    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getHotel_id() {
        return hotel_id;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getRegion() {
        return region;
    }
}


