package com.example.pplki18.grouptravelplanner.old_stuff;

import android.provider.BaseColumns;

public class GroupContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private GroupContract(){}

    /**
     * Inner class that defines constant values for the group table.
     */
    public static final class GroupEntry implements BaseColumns{

        /** Name of database table for user-friend relation */
        public static final String TABLE_NAME = "groups";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_GROUP_NAME = "name";
        public static final String COL_GROUP_IMAGE = "image";
        public static final String COL_GROUP_CREATOR = "creator";
    }
}
