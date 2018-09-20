package com.example.pplki18.grouptravelplanner.data;

import android.provider.BaseColumns;

public class UserGroupContract {

    private UserGroupContract(){}

    public static final class UserGroupEntry implements BaseColumns{

        public static final String TABLE_NAME = "userGroup";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_GROUP_ID = "group_id";
        public static final String COL_USER_ID = "user_id";
    }
}
