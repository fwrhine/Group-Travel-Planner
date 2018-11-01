package com.example.pplki18.grouptravelplanner.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.pplki18.grouptravelplanner.Reminder;
import com.example.pplki18.grouptravelplanner.data.UserContract.UserEntry;
import com.example.pplki18.grouptravelplanner.data.GroupContract.GroupEntry;
import com.example.pplki18.grouptravelplanner.data.UserGroupContract.UserGroupEntry;
import com.example.pplki18.grouptravelplanner.data.FriendsContract.FriendsEntry;
import com.example.pplki18.grouptravelplanner.data.PlanContract.PlanEntry;
import com.example.pplki18.grouptravelplanner.data.EventContract.EventEntry;
//import com.example.pplki18.grouptravelplanner.data.PlanContract.RundownEntry;
//import com.example.pplki18.grouptravelplanner.data.HotelContract.HotelEntry;
//import com.example.pplki18.grouptravelplanner.data.RestaurantContract.RestaurantEntry;
//import com.example.pplki18.grouptravelplanner.data.EntertainmentContract.EntertainmentEntry;
//import com.example.pplki18.grouptravelplanner.data.FlightContract.FlightEntry;
//import com.example.pplki18.grouptravelplanner.data.TrainContract.TrainEntry;
//import com.example.pplki18.grouptravelplanner.data.OtherEventContract.OtherEventEntry;
import com.example.pplki18.grouptravelplanner.data.ReminderContract.ReminderEntry;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    public static final String DATABASE_NAME = "ppl2018.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 9;
    private Context c2;
    /**
     * Constructs a new instance of {@link DatabaseHelper}.
     *
     * @param context of the app
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c2 = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /** Create Strings that contains the SQL statement to create the tables */
        // String to create a table for users
        String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " ("
                + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UserEntry.COL_FULLNAME + " TEXT NOT NULL, "
                + UserEntry.COL_USERNAME + " TEXT UNIQUE NOT NULL, "
                + UserEntry.COL_PASSWORD + " TEXT NOT NULL, "
                + UserEntry.COL_EMAIL + " TEXT UNIQUE NOT NULL, "
                + UserEntry.COL_GENDER + " INTEGER NOT NULL DEFAULT 0, "
                + UserEntry.COL_PHONE + " TEXT DEFAULT \'\', "
                + UserEntry.COL_BIRTHDAY + " TEXT DEFAULT \'\', "
                + UserEntry.COL_PICTURE + " LONGBLOB, "
                + UserEntry.COL_STATUS + " INTEGER NOT NULL DEFAULT 0);";

        // String to create a table for groups
        String SQL_CREATE_GROUP_TABLE = "CREATE TABLE " + GroupEntry.TABLE_NAME + " ("
                + GroupEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GroupEntry.COL_GROUP_NAME + " TEXT NOT NULL, "
                + GroupEntry.COL_GROUP_IMAGE + " LONGBLOB, "
                + GroupEntry.COL_GROUP_CREATOR + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + GroupEntry.COL_GROUP_CREATOR + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "));";

        // String to create a table for user-group relation
        String SQL_CREATE_IN_GROUP_REL = "CREATE TABLE " + UserGroupEntry.TABLE_NAME + " ("
                + UserGroupEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UserGroupEntry.COL_GROUP_ID + " INTEGER, "
                + UserGroupEntry.COL_USER_ID + " INTEGER, "
                + "FOREIGN KEY(" + UserGroupEntry.COL_USER_ID + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "), "
                + "FOREIGN KEY(" + UserGroupEntry.COL_GROUP_ID + ") "
                + " REFERENCES " + GroupEntry.TABLE_NAME + "(" + GroupEntry._ID + "));";

        // String to create a table for user-user relation
        String SQL_CREATE_FRIENDS_REL = "CREATE TABLE " + FriendsEntry.TABLE_NAME + " ("
                + FriendsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FriendsEntry.COL_USER_ID + " INTEGER, "
                + FriendsEntry.COL_FRIEND_ID + " INTEGER, "

                // EDIT
                + FriendsEntry.COL_FRIEND_USERNAME + " TEXT, "

                + "FOREIGN KEY(" + FriendsEntry.COL_USER_ID + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "), "
                + "FOREIGN KEY(" + FriendsEntry.COL_USER_ID + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "));";

        // String to create a table for plan table
        String SQL_CREATE_PLAN_TABLE = "CREATE TABLE " + PlanEntry.TABLE_NAME + " ("
                + PlanEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PlanEntry.COL_PLAN_NAME + " TEXT NOT NULL, "
                + PlanEntry.COL_DESCRIPTION + " TEXT, "
                + PlanEntry.COL_USER_ID + " INTEGER, "
                + PlanEntry.COL_START_DAY + " TEXT, "
                + PlanEntry.COL_END_DAY + " TEXT, "
                + PlanEntry.COL_TOTAL_DAY + " INTEGER, "

                + "FOREIGN KEY(" + PlanEntry.COL_USER_ID + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "));";

        String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + " ("
                + EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EventEntry.COL_PLAN_ID + " INTEGER NOT NULL, "
                + EventEntry.COL_QUERY_ID + " TEXT, "
                + EventEntry.COL_TITLE + " TEXT, "
                + EventEntry.COL_LOCATION + " TEXT, "
                + EventEntry.COL_DESCRIPTION + " TEXT, "
                + EventEntry.COL_DATE + " TEXT, "
                + EventEntry.COL_TIME_START + " TEXT, "
                + EventEntry.COL_TIME_END + " TEXT, "
                + EventEntry.COL_PHONE + " TEXT, "
                + EventEntry.COL_TYPE + " TEXT, "
                + EventEntry.COL_RATING + " TEXT, "
                + EventEntry.COL_WEBSITE + " TEXT, "
                + EventEntry.COL_PRICE + " TEXT, "

                + EventEntry.COL_ORIGIN + " TEXT, "
                + EventEntry.COL_DESTINATION + " TEXT, "
                + EventEntry.COL_DEPARTURE_TIME + " TEXT, "
                + EventEntry.COL_ARRIVAL_TIME + " TEXT, "
                + EventEntry.COL_TRANS_NUMBER + " TEXT, "

                + EventEntry.COL_DATE_CHECK_IN + " TEXT, "
                + EventEntry.COL_DATE_CHECK_OUT + " TEXT, "
                + EventEntry.COL_TIME_CHECK_IN + " TEXT, "
                + EventEntry.COL_TIME_CHECK_OUT + " TEXT, "

                + "FOREIGN KEY(" + EventEntry.COL_PLAN_ID + ")"
                + " REFERENCES " + PlanEntry.TABLE_NAME + "(" + PlanEntry._ID + ")"
                + " ON DELETE CASCADE);";

        //create notification table

        String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " + ReminderEntry.TABLE_NAME + " ("
                + ReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReminderEntry.COL_USER_ID + " INTEGER, "
                + ReminderEntry.COL_EVENT_ID + " INTEGER, "
                + ReminderEntry.COL_ALARM_CHANNEL + " INTEGER, "


                + "FOREIGN KEY(" + ReminderEntry.COL_USER_ID + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "), "
                + "FOREIGN KEY(" + ReminderEntry.COL_USER_ID + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "));";

        // Execute the SQL statements
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_GROUP_TABLE);
        db.execSQL(SQL_CREATE_IN_GROUP_REL);
        db.execSQL(SQL_CREATE_FRIENDS_REL);
        db.execSQL(SQL_CREATE_PLAN_TABLE);
        db.execSQL(SQL_CREATE_EVENT_TABLE);
//        db.execSQL(SQL_CREATE_HOTEL_TABLE);
//        db.execSQL(SQL_CREATE_RESTAURANT_TABLE);
//        db.execSQL(SQL_CREATE_ENTERTAINMENT_TABLE);
//        db.execSQL(SQL_CREATE_FLIGHT_TABLE);
//        db.execSQL(SQL_CREATE_TRAIN_TABLE);
//        db.execSQL(SQL_CREATE_OTHER_EVENT_TABLE);
        db.execSQL(SQL_CREATE_REMINDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GroupEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserGroupEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FriendsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlanEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);
        onCreate(db);
    }

    public int insertUser(String fullname, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("SELECT setval('user_id_seq', (SELECT max(user_id) FROM users))");

//        Log.d("username", username);
//        Log.d("email",  email);

        String query = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE username = \"" + username +
                "\" OR email = \"" + email + "\"";
        Log.d("QUERY", query);
        Cursor cursor = db.rawQuery(query, null);
        Log.d("STATE", cursor.getCount() + "");
        if(cursor.getCount() > 0) {
            cursor.close();
            return 2;
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COL_FULLNAME, fullname);
        contentValues.put(UserEntry.COL_USERNAME, username);
        contentValues.put(UserEntry.COL_PASSWORD, password);
        contentValues.put(UserEntry.COL_EMAIL, email);
        contentValues.put(UserEntry.COL_GENDER, 0);
        contentValues.put(UserEntry.COL_PHONE, "");
        contentValues.put(UserEntry.COL_BIRTHDAY, "");
        contentValues.putNull(UserEntry.COL_PICTURE);
        contentValues.put(UserEntry.COL_STATUS, 0);
        long result = db.insert(UserEntry.TABLE_NAME, null, contentValues);
        if(result == -1)
            return 0;
        else
            return 1;
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    ///////////////////////////////////////////// convert image view to byte array /////////////////////////////////////////////

    //convert image view to byte array
//    public static byte[] imageViewToByte() {
//        try {
//            Bitmap bitmap = getBitmapFromDrawable(image.getDrawable());
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            return byteArray;
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//            return new byte[0];
//        }
//    }

    //convert drawable to bitmap
    @NonNull
    private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    private byte[] bmpInDrawableToByteArr(Context context, Integer r){
        Drawable d = context.getResources().getDrawable(r);
        Bitmap b = getBitmapFromDrawable(d);
        return getBytesFromBitmap(b);
    }

}
