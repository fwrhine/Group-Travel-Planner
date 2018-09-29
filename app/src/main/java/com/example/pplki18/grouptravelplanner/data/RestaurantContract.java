package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class RestaurantContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private RestaurantContract(){}

    /**
     * Inner class that defines constant values for the restaurant table.
     */
    public static final class RestaurantEntry implements BaseColumns {

        /** Name of database table for restaurant relation */
        public static final String TABLE_NAME = "restaurants";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_RUNDOWN_ID = "rundown_id";
        public static final String COL_RESTAURANT_NAME = "restaurant_name";
        public static final String COL_RESTAURANT_LOCATION = "restaurant_location";
        public static final String COL_RESTAURANT_DESCRIPTION = "restaurant_description";
        public static final String COL_DATE = "date";
        public static final String COL_TIME = "time";

    }
}
