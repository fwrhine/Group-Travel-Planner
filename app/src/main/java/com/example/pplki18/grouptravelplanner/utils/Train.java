package com.example.pplki18.grouptravelplanner.utils;

public class Train {
    public String trainName;
    public String flightNumber;
    public String departureTime;
    public String arrivalTime;
    public String price;
    public String departureCity;
    public String arrivalCity;

    public Train(String trainName, String departureTime, String arrivalTime,
                  String price, String departureCity, String arrivalCity) {
        this.trainName=trainName;
        this.departureTime=departureTime;
        this.departureCity=departureCity;
        this.arrivalTime=arrivalTime;
        this.arrivalCity=arrivalCity;
        this.price=price;
    }
}