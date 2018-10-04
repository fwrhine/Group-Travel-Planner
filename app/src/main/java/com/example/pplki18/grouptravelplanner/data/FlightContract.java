package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class FlightContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private FlightContract(){}

    /**
     * Inner class that defines constant values for the flight table.
     */
    public static final class FlightEntry implements BaseColumns {

        /** Name of database table for flight relation */
        public static final String TABLE_NAME = "flights";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_PLAN_ID = "rundown_id";
        public static final String COL_FLIGHT_NAME = "name";
        public static final String COL_FLIGHT_ORIGIN = "origin_airport";
        public static final String COL_FLIGHT_DESTINATION = "destination_airport";
        public static final String COL_FLIGHT_DESCRIPTION = "flight_description";
        public static final String COL_DATE = "date";
        public static final String COL_TIME = "time";

    }
}
