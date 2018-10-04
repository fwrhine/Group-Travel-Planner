package com.example.pplki18.grouptravelplanner;

import java.util.List;

public class Reminder {

    String destination;
    String date;
    Integer channel;

    //constructor

    public Reminder(String destination, String date, Integer channel) {
        this.destination = destination;
        this.date = date;
        this.channel = channel;
    }

    //setters
    public void setDestination(String d) {
        this.destination = d;
    }

    public void setDate(String d) {
        this.date = d;
    }

    //getters
    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

}
