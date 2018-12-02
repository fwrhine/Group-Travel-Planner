package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class ReminderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ReminderContract(){}

    /**
     * Inner class that defines constant values for the user-friend relation.
     */
    public static final class ReminderEntry implements BaseColumns {

        /** Name of database table for user-friend relation */
        public static final String TABLE_NAME = "notifications";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_USER_ID = "user_id";
        public static final String COL_EVENT_ID = "event_id";
        public static final String COL_ALARM_CHANNEL = "alarm_channel";
    }
}
