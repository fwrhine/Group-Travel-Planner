package com.example.pplki18.grouptravelplanner.utils;

import java.util.ArrayList;

public class Hotel {
    private String hotel_id;
    private String name;
    private String rating;
    private String address;
    private String phone_number;
    private String photo;
    private String website;
    private String price;
    private String description;
    private ArrayList<ArrayList<String>> amenities;


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

    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmenities(ArrayList<ArrayList<String>> amenities) {
        this.amenities = amenities;
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

    public String getPhone_number() { return phone_number; }

    public String getPhoto() {
        return photo;
    }

    public String getWebsite() {
        return website;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<ArrayList<String>> getAmenities() {
        return amenities;
    }

    @Override
    public String toString() {
        return name;
    }
}


