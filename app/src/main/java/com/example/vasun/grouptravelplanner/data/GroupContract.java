package com.example.vasun.grouptravelplanner.data;

import android.provider.BaseColumns;

public class GroupContract {

    private GroupContract(){}

    public static final class GroupEntry implements BaseColumns{

        public static final String TABLE_NAME = "groups";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_GROUP_NAME = "name";
        public static final String COL_GROUP_IMAGE = "image";
    }
}
