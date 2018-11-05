package com.example.pplki18.grouptravelplanner;

import java.util.Date;

public class Reminder {

    String destination;
    Date date;
    Long eventID;
    Integer alarmChannel;

    //constructor

    public Reminder(String destination, Date date, Long id) {
        this.destination = destination;
        this.date = date;
        this.eventID = id;
    }

    //setters
    public void setDestination(String d) {
        this.destination = d;
    }

    public void setDate(Date d) {
        this.date = d;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    //getters
    public String getDestination() {
        return destination;
    }

    public Date getDate() {
        return date;
    }

    public Long getEventID() {
        return eventID;
    }
}
