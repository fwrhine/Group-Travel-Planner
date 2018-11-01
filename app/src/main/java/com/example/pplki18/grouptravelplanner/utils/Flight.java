package com.example.pplki18.grouptravelplanner.utils;

public class Flight {
    public final String airlineName;
    public final String flightNumber;
    public final String departureTime;
    public final String arrivalTime;
    public final String price;
    public final String departureCity;
    public final String arrivalCity;

    public Flight(String airlineName, String flightNumber, String departureTime, String arrivalTime,
                  String price, String departureCity, String arrivalCity) {
        this.airlineName=airlineName;
        this.flightNumber=flightNumber;
        this.departureTime=departureTime;
        this.departureCity=departureCity;
        this.arrivalTime=arrivalTime;
        this.arrivalCity=arrivalCity;
        this.price=price;
    }
}