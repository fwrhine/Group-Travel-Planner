package com.example.pplki18.grouptravelplanner.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Event implements Parcelable, Comparable<Event> {
    private String event_id;
    private String plan_id;

    private String creator_id;
    private String query_id;
    private String title;
    private String location;
    private String description;
    private String date;
    private String time_start;
    private String time_end;
    private String phone;
    private String type;
    private String rating;
    private String website;
    private String price;

    private String origin;
    private String destination;
//    private String departure_time;
//    private String arrival_time;
    private String transport_number;

    // for hotel
    private String date_check_in;
    private String date_check_out;
    private String time_check_in;
    private String time_check_out;

    private String plan_name;

    // empty constructor
    public Event() {}

    public Event(String time_start, String time_end) {
        this.time_start = time_start;
        this.time_end = time_end;
    }

    public Event(String title, String date, String time_start, String time_end, String type) {
        this.title = title;
        this.date = date;
        this.type = type;
        this.time_start = time_start;
        this.time_end = time_end;
    }

    protected Event(Parcel in) {
        creator_id = in.readString();
        event_id = in.readString();
        query_id = in.readString();
        title = in.readString();
        location = in.readString();
        description = in.readString();
        date = in.readString();
        time_start = in.readString();
        time_end = in.readString();
        phone = in.readString();
        type = in.readString();
        rating = in.readString();
        website = in.readString();
        price = in.readString();
        origin = in.readString();
        destination = in.readString();
//        departure_time = in.readString();
//        arrival_time = in.readString();
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

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String id) {
        this.event_id = id;
    }

    public static Creator<Event> getCREATOR() {
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
        if (type.equals("hotel")) {
            return "";
        } else {
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

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id){
        this.plan_id = plan_id;
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
        return time_start;
    }

    public void setDeparture_time(String departure_time) {
        this.time_start = departure_time;
    }

    public String getArrival_time() {
        return time_end;
    }

    public void setArrival_time(String arrival_time) {
        this.time_end = arrival_time;
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
        parcel.writeString(event_id);
        parcel.writeString(query_id);
        parcel.writeString(title);
        parcel.writeString(location);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(time_start);
        parcel.writeString(time_end);
        parcel.writeString(phone);
        parcel.writeString(type);
        parcel.writeString(rating);
        parcel.writeString(website);
        parcel.writeString(price);
        parcel.writeString(origin);
        parcel.writeString(destination);
//        parcel.writeString(departure_time);
//        parcel.writeString(arrival_time);
        parcel.writeString(transport_number);
        parcel.writeString(date_check_in);
        parcel.writeString(date_check_out);
        parcel.writeString(time_check_in);
        parcel.writeString(time_check_out);
        parcel.writeString(plan_name);
    }

    @Override
    public int compareTo(@NonNull Event event) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date this_start = null;
            Date this_end = null;
            Date event_start = null;
            Date event_end = null;
//            if (type.equals("restaurants") || type.equals("attractions") || type.equals("custom")) {
                this_start = format.parse(this.getTime_start());
                this_end = format.parse(this.getTime_end());
                event_start = format.parse(event.getTime_start());
                event_end = format.parse(event.getTime_end());
//            } else if (type.equals("flights") || type.equals("trains")) {
//                this_start = format.parse(this.getDeparture_time());
//                this_end = format.parse(this.getArrival_time());
//                event_start = format.parse(event.getDeparture_time());
//                event_end = format.parse(event.getArrival_time());
//            } else if (type.equals("hotels")) {
//
//            }

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