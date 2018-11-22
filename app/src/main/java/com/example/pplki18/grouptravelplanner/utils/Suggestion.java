package com.example.pplki18.grouptravelplanner.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Suggestion implements Parcelable {
    private String suggestion_id;
    private String group_id;

    private String creator_id;
    private String query_id;
    private String title;
    private String location;
    private String description;
    private String phone;
    private String type;
    private String rating;
    private String website;
    private String price;

    private String origin;
    private String destination;
    private String transport_number;

    private String plan_name;

    public Suggestion() {}

    public Suggestion(String title, String type) {
        this.title = title;
        this.type = type;
    }

    protected Suggestion(Parcel in) {
        creator_id = in.readString();
        suggestion_id = in.readString();
        query_id = in.readString();
        title = in.readString();
        location = in.readString();
        description = in.readString();
        phone = in.readString();
        type = in.readString();
        rating = in.readString();
        website = in.readString();
        price = in.readString();
        origin = in.readString();
        destination = in.readString();
        transport_number = in.readString();
        plan_name = in.readString();
    }

    public static final Creator<Suggestion> CREATOR = new Creator<Suggestion>() {
        @Override
        public Suggestion createFromParcel(Parcel in) {
            return new Suggestion(in);
        }

        @Override
        public Suggestion[] newArray(int size) {
            return new Suggestion[size];
        }
    };

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getSuggestion_id() {
        return suggestion_id;
    }

    public void setSuggestion_id(String id) {
        this.suggestion_id = id;
    }

    public static Creator<Suggestion> getCREATOR() {
        return CREATOR;
    }

    public String getQuery_id() {
        return query_id;
    }

    public void setQuery_id(String query_id) {
        this.query_id = query_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id){
        this.group_id = group_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTransport_number() {
        return transport_number;
    }

    public void setTransport_number(String transport_number) {
        this.transport_number = transport_number;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getWebsite() { return website; }

    public void setWebsite(String website) { this.website = website; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(creator_id);
        parcel.writeString(suggestion_id);
        parcel.writeString(query_id);
        parcel.writeString(title);
        parcel.writeString(location);
        parcel.writeString(description);
        parcel.writeString(phone);
        parcel.writeString(type);
        parcel.writeString(rating);
        parcel.writeString(website);
        parcel.writeString(price);
        parcel.writeString(origin);
        parcel.writeString(destination);
        parcel.writeString(transport_number);
        parcel.writeString(plan_name);
    }
}
