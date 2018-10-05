package com.example.pplki18.grouptravelplanner.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    private String title;
    private String location;
    private String description;
    private String date;
    private Date time_start;
    private Date time_end;
    private String phone;
    private String type;

    public String origin;
    public String destination;
    public String departure_time;
    public String arrival_time;
    public String transport_number;

    // for hotel
    public String date_check_in;
    public String date_check_out;
    public String time_check_in;
    public String time_check_out;

    // empty constructor
    public Event() {}

    public Event(String title, Date time_start, Date time_end, String type) {
        this.title = title;
        this.time_start = time_start;
        this.time_end = time_end;
        this.type = type;
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

    public Date getTime_start() {
        return time_start;
    }

    public void setTime_start(Date time_start) {
        this.time_start = time_start;
    }

    public Date getTime_end() {
        return time_end;
    }

    public void setTime_end(Date time_end) {
        this.time_end = time_end;
    }

    public String getTotal_time() {
        long diff = time_end.getTime() - time_start.getTime();

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
}
