package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class EventContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private EventContract(){}

    /**
     * Inner class that defines constant values for the hotel table.
     */
    public static final class EventEntry implements BaseColumns {

        /** Name of database table for hotel relation */
        public static final String TABLE_NAME = "events";

        // for all
        public static final String _ID = BaseColumns._ID;
        public static final String COL_QUERY_ID = "query_id";
        public static final String COL_PLAN_ID = "plan_id";
        public static final String COL_TITLE = "event_title";
        public static final String COL_LOCATION = "event_location";
        public static final String COL_DESCRIPTION = "event_description";
        public static final String COL_DATE = "event_date";
        public static final String COL_TIME_START = "event_time_start";
        public static final String COL_TIME_END = "event_time_end";
        public static final String COL_PHONE = "event_phone";
        public static final String COL_TYPE = "event_type";
        public static final String COL_RATING = "event_rating";

        // for flight and train
        public static final String COL_ORIGIN = "origin";
        public static final String COL_DESTINATION = "destination";
        public static final String COL_DEPARTURE_TIME = "departure_time";
        public static final String COL_ARRIVAL_TIME = "arrival_time";
        public static final String COL_TRANS_NUMBER = "transport_number";

        // for hotel
        public static final String COL_DATE_CHECK_IN = "date_check_in";
        public static final String COL_DATE_CHECK_OUT = "date_check_out";
        public static final String COL_TIME_CHECK_IN = "time_check_in";
        public static final String COL_TIME_CHECK_OUT = "time_check_out";


    }
}
