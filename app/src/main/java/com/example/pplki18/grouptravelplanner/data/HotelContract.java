package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class HotelContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private HotelContract(){}

    /**
     * Inner class that defines constant values for the hotel table.
     */
    public static final class HotelEntry implements BaseColumns {

        /** Name of database table for hotel relation */
        public static final String TABLE_NAME = "hotels";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_PLAN_ID = "rundown_id";
        public static final String COL_HOTEL_NAME = "hotel_name";
        public static final String COL_HOTEL_LOCATION = "hotel_location";
        public static final String COL_HOTEL_DESCRIPTION = "hotel_description";
        public static final String COL_DATE_CHECK_IN = "date_check_in";
        public static final String COL_DATE_CHECK_OUT = "date_check_out";
        public static final String COL_TIME_CHECK_IN = "time_check_in";
        public static final String COL_TIME_CHECK_OUT = "time_check_out";


    }
}
