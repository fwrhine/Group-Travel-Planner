package com.example.pplki18.grouptravelplanner.utils;

public class Flight {
    public String airlineName;
    public String flightNumber;
    public String departureTime;
    public String arrivalTime;
    public String price;
    public String departureCity;
    public String arrivalCity;

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