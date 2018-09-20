package com.example.vasun.grouptravelplanner.data;

import android.provider.BaseColumns;

public class UserContract {

    private UserContract(){}

    public static final class UserEntry implements BaseColumns{

        public static final String TABLE_NAME = "users";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_FULLNAME = "fullname";
        public static final String COL_USERNAME = "username";
        public static final String COL_PASSWORD = "password";
        public static final String COL_EMAIL = "email";
        public static final String COL_GENDER = "gender";
        public static final String COL_PHONE = "phone_no";
        public static final String COL_PICTURE = "display_picture";
        public static final String COL_STATUS = "status";

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        public static final int STATUS_NOT_ON_TRIP = 0;
        public static final int STATUS_ON_TRIP = 1;
    }
}
