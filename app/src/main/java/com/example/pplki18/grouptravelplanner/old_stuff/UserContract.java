package com.example.pplki18.grouptravelplanner.old_stuff;

import android.provider.BaseColumns;

public class UserContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private UserContract(){}

    /**
     * Inner class that defines constant values for the user table.
     */
    public static final class UserEntry implements BaseColumns{

        /** Name of database table for user-friend relation */
        public static final String TABLE_NAME = "users";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_FULLNAME = "fullname";
        public static final String COL_USERNAME = "username";
        public static final String COL_PASSWORD = "password";
        public static final String COL_EMAIL = "email";
        public static final String COL_GENDER = "gender";
        public static final String COL_PHONE = "phone_no";
        public static final String COL_BIRTHDAY = "birthday";
        public static final String COL_PICTURE = "display_picture";
        public static final String COL_STATUS = "status";

        /** Constant values for user's gender */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        /** Constant values for user's trip status */
        public static final int STATUS_NOT_ON_TRIP = 0;
        public static final int STATUS_ON_TRIP = 1;
    }
}
