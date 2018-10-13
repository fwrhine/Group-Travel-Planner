package com.example.pplki18.grouptravelplanner.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Parcelable {
    private String title;
    private String location;
    private String description;
    private String date;
    private String time_start;
    private String time_end;
    private String phone;
    private String type;
    private String rating;

    private String origin;
    private String destination;
    private String departure_time;
    private String arrival_time;
    private String transport_number;

    // for hotel
    private String date_check_in;
    private String date_check_out;
    private String time_check_in;
    private String time_check_out;

    private String plan_name;

    // empty constructor
    public Event() {}

    public Event(String title, String time_start, String time_end, String type) {
        this.title = title;
        this.time_start = time_start;
        this.time_end = time_end;
        this.type = type;
    }

    protected Event(Parcel in) {
        title = in.readString();
        location = in.readString();
        description = in.readString();
        date = in.readString();
        time_start = in.readString();
        time_end = in.readString();
        phone = in.readString();
        type = in.readString();
        rating = in.readString();
        origin = in.readString();
        destination = in.readString();
        departure_time = in.readString();
        arrival_time = in.readString();
        transport_number = in.readString();
        date_check_in = in.readString();
        date_check_out = in.readString();
        time_check_in = in.readString();
        time_check_out = in.readString();
        plan_name = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

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
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getTransport_number() {
        return transport_number;
    }

    public void setTransport_number(String transport_number) {
        this.transport_number = transport_number;
    }

    public String getDate_check_in() {
        return date_check_in;
    }

    public void setDate_check_in(String date_check_in) {
        this.date_check_in = date_check_in;
    }

    public String getDate_check_out() {
        return date_check_out;
    }

    public void setDate_check_out(String date_check_out) {
        this.date_check_out = date_check_out;
    }

    public String getTime_check_in() {
        return time_check_in;
    }

    public void setTime_check_in(String time_check_in) {
        this.time_check_in = time_check_in;
    }

    public String getTime_check_out() {
        return time_check_out;
    }

    public void setTime_check_out(String time_check_out) {
        this.time_check_out = time_check_out;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(location);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(time_start);
        parcel.writeString(time_end);
        parcel.writeString(phone);
        parcel.writeString(type);
        parcel.writeString(rating);
        parcel.writeString(origin);
        parcel.writeString(destination);
        parcel.writeString(departure_time);
        parcel.writeString(arrival_time);
        parcel.writeString(transport_number);
        parcel.writeString(date_check_in);
        parcel.writeString(date_check_out);
        parcel.writeString(time_check_in);
        parcel.writeString(time_check_out);
        parcel.writeString(plan_name);
    }
}
