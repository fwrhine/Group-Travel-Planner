package com.example.pplki18.grouptravelplanner.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Suggestion implements Parcelable, Comparable<Suggestion> {
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

    private String time_start;
    private String time_end;

    private String origin;
    private String destination;
    private String transport_number;

    private String plan_id;
    private String plan_name;
    private String plan_date;


    public Suggestion() {}

    public Suggestion(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public Suggestion(String title, String time_start, String time_end, String type) {
        this.title = title;
        this.type = type;
        this.time_start = time_start;
        this.time_end = time_end;
    }

    private Suggestion(Parcel in) {
        creator_id = in.readString();
        suggestion_id = in.readString();
        query_id = in.readString();
        title = in.readString();
        location = in.readString();
        description = in.readString();

        time_start = in.readString();
        time_end = in.readString();

        phone = in.readString();
        type = in.readString();
        rating = in.readString();
        website = in.readString();
        price = in.readString();
        origin = in.readString();
        destination = in.readString();
        transport_number = in.readString();
        plan_id = in.readString();
        plan_name = in.readString();
        plan_date = in.readString();
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

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getPlan_date() {
        return plan_date;
    }

    public void setPlan_date(String plan_date) {
        this.plan_date = plan_date;
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

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getTotal_time() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        long diff = 0;

        try {
            diff = format.parse(time_end).getTime() - format.parse(time_start).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        return diffHours + " hours, " + diffMinutes + " minutes";
    }

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

        parcel.writeString(time_start);
        parcel.writeString(time_end);

        parcel.writeString(phone);
        parcel.writeString(type);
        parcel.writeString(rating);
        parcel.writeString(website);
        parcel.writeString(price);
        parcel.writeString(origin);
        parcel.writeString(destination);
        parcel.writeString(transport_number);
        parcel.writeString(plan_id);
        parcel.writeString(plan_name);
        parcel.writeString(plan_date);
    }

    @Override
    public int compareTo(@NonNull Suggestion suggest) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date this_start = format.parse(this.getTime_start());
            Date this_end = format.parse(this.getTime_end());
            Date event_start = format.parse(suggest.getTime_start());
            Date event_end = format.parse(suggest.getTime_end());

            if (this_start.getTime() > event_start.getTime()) {
                return 1;
            } else if (this_start.getTime() < event_start.getTime()) {
                return -1;
            } else {
                return (int) (this_end.getTime() - event_end.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
