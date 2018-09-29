package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class EntertainmentContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private EntertainmentContract(){}

    /**
     * Inner class that defines constant values for the entertainment table.
     */
    public static final class EntertainmentEntry implements BaseColumns {

        /** Name of database table for entertainment relation */
        public static final String TABLE_NAME = "entertainments";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_PLAN_ID = "rundown_id";
        public static final String COL_ENTERTAINMENT_NAME = "entertainment_name";
        public static final String COL_ENTERTAINMENT_LOCATION = "entertainment_location";
        public static final String COL_ENTERTAINMENT_DESCRIPTION = "entertainment_description";
        public static final String COL_DATE = "date";
        public static final String COL_TIME = "time";

    }
}
