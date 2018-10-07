package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class PlanContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private PlanContract(){}

    /**
     * Inner class that defines constant values for the plan table.
     */
    public static final class PlanEntry implements BaseColumns {

        /** Name of database table for user-friend relation */
        public static final String TABLE_NAME = "plans";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_PLAN_NAME = "plan_name";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_TOTAL_DAY = "total_day";
        public static final String COL_START_DAY = "start_day";
        public static final String COL_END_DAY = "end_day";

    }

}
