package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class FriendsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private FriendsContract(){}

    /**
     * Inner class that defines constant values for the user-friend relation.
     */
    public static final class FriendsEntry implements BaseColumns{

        /** Name of database table for user-friend relation */
        public static final String TABLE_NAME = "friends";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_USER_ID = "user_id";
        public static final String COL_FRIEND_ID = "friend_id";
        public static final String COL_FRIEND_USERNAME = "username";
    }
}
