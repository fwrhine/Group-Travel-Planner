package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class RundownContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private RundownContract(){}

    /**
     * Inner class that defines constant values for the rundown table.
     */
    public static final class RundownEntry implements BaseColumns {

        /** Name of database table for user-friend relation */
        public static final String TABLE_NAME = "rundowns";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_USER_ID = "user_id";
        public static final String COL_TOTAL_DAY = "total_day";
        public static final String COL_START_DAY = "start_day";
        public static final String COL_END_DAY = "end_day";

    }

}
