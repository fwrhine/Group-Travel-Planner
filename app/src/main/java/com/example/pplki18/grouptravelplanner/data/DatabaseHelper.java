package com.example.pplki18.grouptravelplanner.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.ImageView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.UserContract.UserEntry;
import com.example.pplki18.grouptravelplanner.data.GroupContract.GroupEntry;
import com.example.pplki18.grouptravelplanner.data.UserGroupContract.UserGroupEntry;
import com.example.pplki18.grouptravelplanner.data.FriendsContract.FriendsEntry;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    public static final String DATABASE_NAME = "ppl2018.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 5;
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

        // Execute the SQL statements
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_GROUP_TABLE);
        db.execSQL(SQL_CREATE_IN_GROUP_REL);
        db.execSQL(SQL_CREATE_FRIENDS_REL);

        //insert dummy
        ContentValues dummyUser1 = new ContentValues();
        dummyUser1.put(UserEntry.COL_FULLNAME, "dummy1");
        dummyUser1.put(UserEntry.COL_USERNAME, "dummy1");
        dummyUser1.put(UserEntry.COL_PASSWORD, "dummy1");
        dummyUser1.put(UserEntry.COL_EMAIL, "dummy1@gmail.com");
        dummyUser1.put(UserEntry.COL_GENDER, 0);
        dummyUser1.put(UserEntry.COL_PHONE, 0);
        dummyUser1.put(UserEntry.COL_PICTURE, 0);
        dummyUser1.put(UserEntry.COL_STATUS, 0);
        db.insert(UserEntry.TABLE_NAME, null, dummyUser1);

        ContentValues dummyUser2 = new ContentValues();
        dummyUser2.put(UserEntry.COL_FULLNAME, "dummy2");
        dummyUser2.put(UserEntry.COL_USERNAME, "dummy2");
        dummyUser2.put(UserEntry.COL_PASSWORD, "dummy2");
        dummyUser2.put(UserEntry.COL_EMAIL, "dummy2@gmail.com");
        dummyUser2.put(UserEntry.COL_GENDER, 0);
        dummyUser2.put(UserEntry.COL_PHONE, 0);
        dummyUser2.put(UserEntry.COL_PICTURE, 0);
        dummyUser2.put(UserEntry.COL_STATUS, 0);
        db.insert(UserEntry.TABLE_NAME, null, dummyUser2);

        ContentValues dummyUser3 = new ContentValues();
        dummyUser3.put(UserEntry.COL_FULLNAME, "dummy3");
        dummyUser3.put(UserEntry.COL_USERNAME, "dummy3");
        dummyUser3.put(UserEntry.COL_PASSWORD, "dummy3");
        dummyUser3.put(UserEntry.COL_EMAIL, "dummy3@gmail.com");
        dummyUser3.put(UserEntry.COL_GENDER, 0);
        dummyUser3.put(UserEntry.COL_PHONE, 0);
        dummyUser3.put(UserEntry.COL_PICTURE, 0);
        dummyUser3.put(UserEntry.COL_STATUS, 0);
        db.insert(UserEntry.TABLE_NAME, null, dummyUser3);

        ContentValues dummyUser4 = new ContentValues();
        dummyUser4.put(UserEntry.COL_FULLNAME, "dummy4");
        dummyUser4.put(UserEntry.COL_USERNAME, "dummy4");
        dummyUser4.put(UserEntry.COL_PASSWORD, "dummy4");
        dummyUser4.put(UserEntry.COL_EMAIL, "dummy4@gmail.com");
        dummyUser4.put(UserEntry.COL_GENDER, 0);
        dummyUser4.put(UserEntry.COL_PHONE, 0);
        dummyUser4.put(UserEntry.COL_PICTURE, 0);
        dummyUser4.put(UserEntry.COL_STATUS, 0);
        db.insert(UserEntry.TABLE_NAME, null, dummyUser4);

        ContentValues dummyUser5 = new ContentValues();
        dummyUser5.put(UserEntry.COL_FULLNAME, "dummy5");
        dummyUser5.put(UserEntry.COL_USERNAME, "dummy5");
        dummyUser5.put(UserEntry.COL_PASSWORD, "dummy5");
        dummyUser5.put(UserEntry.COL_EMAIL, "dummy5@gmail.com");
        dummyUser5.put(UserEntry.COL_GENDER, 0);
        dummyUser5.put(UserEntry.COL_PHONE, 0);
        dummyUser5.put(UserEntry.COL_PICTURE, 0);
        dummyUser5.put(UserEntry.COL_STATUS, 0);
        db.insert(UserEntry.TABLE_NAME, null, dummyUser5);

        Drawable d = c2.getResources().getDrawable(R.drawable.group_pic);
        Bitmap b = getBitmapFromDrawable(d);

        ContentValues dummyGroup = new ContentValues();
        dummyGroup.put(GroupEntry.COL_GROUP_CREATOR, "dummy5");
        dummyGroup.put(GroupEntry.COL_GROUP_IMAGE, bmpInDrawableToByteArr(c2, R.drawable.group_pic));
        dummyGroup.put(GroupEntry.COL_GROUP_NAME, "dummyGroup");
        db.insert(UserEntry.TABLE_NAME, null, dummyUser5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GroupEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserGroupEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FriendsEntry.TABLE_NAME);
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
        contentValues.putNull(UserEntry.COL_PICTURE); // contentValues.put(UserEntry.COL_PICTURE, 0);
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
