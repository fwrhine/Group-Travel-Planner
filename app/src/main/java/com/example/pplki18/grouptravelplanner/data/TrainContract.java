package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class TrainContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private TrainContract(){}

    /**
     * Inner class that defines constant values for the trains table.
     */
    public static final class TrainEntry implements BaseColumns {

        /** Name of database table for user-friend relation */
        public static final String TABLE_NAME = "trains";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_RUNDOWN_ID = "rundown_id";
        public static final String COL_TRAIN_NAME = "_name";
        public static final String COL_TRAIN_ORIGIN = "origin_station";
        public static final String COL_TRAIN_DESTINATION = "destination_station";
        public static final String COL_TRAIN_DESCRIPTION = "description";
        public static final String COL_DATE = "date";
        public static final String COL_TIME = "time";

    }
}
