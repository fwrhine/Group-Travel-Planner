package com.example.pplki18.grouptravelplanner;

import java.util.List;

public class Reminder {

    String destination;
    String date;
    Long eventID;
    Integer alarmChannel;

    //constructor

    public Reminder(String destination, String date, Long id, Integer channel) {
        this.destination = destination;
        this.date = date;
        this.eventID = id;
        this.alarmChannel = channel;
    }

    //setters
    public void setDestination(String d) {
        this.destination = d;
    }

    public void setDate(String d) {
        this.date = d;
    }

    public void setChannel(Integer c) {
        this.alarmChannel = c;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    //getters
    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public Integer getAlarmChannel() {
        return alarmChannel;
    }

    public Long getEventID() {
        return eventID;
    }
}
