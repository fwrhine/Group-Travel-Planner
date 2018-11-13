package com.example.pplki18.grouptravelplanner.old_stuff;

import android.provider.BaseColumns;

public class UserGroupContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private UserGroupContract(){}

    /**
     * Inner class that defines constant values for the user-group relation.
     */
    public static final class UserGroupEntry implements BaseColumns{

        /** Name of database table for user-friend relation */
        public static final String TABLE_NAME = "userGroup";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_GROUP_ID = "group_id";
        public static final String COL_USER_ID = "user_id";
    }
}
