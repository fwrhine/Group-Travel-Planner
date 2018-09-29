package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class OtherEventContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private OtherEventContract(){}

    /**
     * Inner class that defines constant values for the other events table.
     */
    public static final class OtherEventEntry implements BaseColumns {

        /** Name of database table for other events table */
        public static final String TABLE_NAME = "other_events";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_PLAN_ID = "rundown_id";
        public static final String COL_OTHER_EVENT_NAME = "other_event_name";
        public static final String COL_OTHER_EVENT_LOCATION = "other_event_location";
        public static final String COL_OTHER_EVENT_DESCRIPTION = "other_event_description";
        public static final String COL_DATE = "date";
        public static final String COL_TIME = "time";

    }
}
