package com.example.pplki18.grouptravelplanner.data;

import java.util.List;

public class Hotel {
    private String hotel_id;
    private String name;
    private String rating;
    private String address;
    private String photo;
    private String website;
    private String price;


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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getAddress() {
        return address;
    }

    public String getPhoto() {
        return photo;
    }

    public String getWebsite() {
        return website;
    }

    public String getPrice() {
        return price;
    }
}


