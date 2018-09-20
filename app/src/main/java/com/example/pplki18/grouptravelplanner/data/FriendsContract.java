package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class FriendsContract {

    private FriendsContract(){}

    public static final class FriendsEntry implements BaseColumns{

        public static final String TABLE_NAME = "friends";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_USER_ID = "user_id";
        public static final String COL_FRIEND_ID = "friend_id";
    }
}
