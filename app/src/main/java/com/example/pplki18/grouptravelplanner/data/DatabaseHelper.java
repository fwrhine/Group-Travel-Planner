package com.example.pplki18.grouptravelplanner.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.pplki18.grouptravelplanner.Group;
import com.example.pplki18.grouptravelplanner.User;
import com.example.pplki18.grouptravelplanner.data.UserContract.UserEntry;
import com.example.pplki18.grouptravelplanner.data.GroupContract.GroupEntry;
import com.example.pplki18.grouptravelplanner.data.UserGroupContract.UserGroupEntry;
import com.example.pplki18.grouptravelplanner.data.FriendsContract.FriendsEntry;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    public static final String DATABASE_NAME = "ppl2018.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DatabaseHelper}.
     *
     * @param context of the app
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                + UserEntry.COL_PHONE + " INTEGER NOT NULL DEFAULT 0, "
                + UserEntry.COL_PICTURE + " LONGBLOB, "
                + UserEntry.COL_STATUS + " INTEGER NOT NULL DEFAULT 0);";

        // String to create a table for groups
        String SQL_CREATE_GROUP_TABLE = "CREATE TABLE " + GroupEntry.TABLE_NAME + " ("
                + GroupEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GroupEntry.COL_GROUP_NAME + " TEXT NOT NULL, "
                + GroupEntry.COL_GROUP_IMAGE + " LONGBLOB);";

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
                + "FOREIGN KEY(" + FriendsEntry.COL_USER_ID + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "), "
                + "FOREIGN KEY(" + FriendsEntry.COL_USER_ID + ")"
                + " REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "));";

        // Execute the SQL statements
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_GROUP_TABLE);
        db.execSQL(SQL_CREATE_IN_GROUP_REL);
        db.execSQL(SQL_CREATE_FRIENDS_REL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GroupEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserGroupEntry.TABLE_NAME);
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

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COL_FULLNAME, fullname);
        contentValues.put(UserEntry.COL_USERNAME, username);
        contentValues.put(UserEntry.COL_PASSWORD, password);
        contentValues.put(UserEntry.COL_EMAIL, email);
        contentValues.put(UserEntry.COL_GENDER, 0);
        contentValues.put(UserEntry.COL_PHONE, 0);
        contentValues.put(UserEntry.COL_PICTURE, 0);
        contentValues.put(UserEntry.COL_STATUS, 0);
        long result = db.insert(UserEntry.TABLE_NAME, null, contentValues);
        if(result == -1)
            return 0;
        else
            return 1;
    }

    public long insertGroup(Group group, ArrayList<Long> user_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupEntry.COL_GROUP_NAME, group.getGroup_name());
        values.put(GroupEntry.COL_GROUP_IMAGE, group.getGroup_image());

        // insert row
        long group_id = db.insert(GroupEntry.TABLE_NAME, null, values);

        // assigning users to groups
        for (long user_id : user_ids) {
            insertUserGroup(group_id, user_id);
        }

        return group_id;
    }

    public long insertUserGroup(long group_id, long user_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserGroupEntry.COL_GROUP_ID, group_id);
        contentValues.put(UserGroupEntry.COL_USER_ID, user_id);

        long id = db.insert(UserGroupEntry.TABLE_NAME, null, contentValues);

        return id;
    }

    /*
     * Get all groups
     * */
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<Group>();
        String selectQuery = "SELECT * FROM " + GroupEntry.TABLE_NAME;

        Log.e("GROUPS", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Group group = new Group();
                group.setGroup_name((c.getString(c.getColumnIndex(GroupEntry.COL_GROUP_NAME))));
                group.setGroup_image(c.getBlob(c.getColumnIndex(GroupEntry.COL_GROUP_IMAGE)));

                // adding to group list
                groups.add(group);
            } while (c.moveToNext());
        }

        return groups;
    }

    /*
     * Get all users
     * */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();
        String selectQuery = "SELECT * FROM " + UserEntry.TABLE_NAME;

        Log.e("USERS", selectQuery);


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User user = new User();
                user.setUser_name((c.getString(c.getColumnIndex(UserEntry.COL_FULLNAME))));

                // adding to group list
                users.add(user);
            } while (c.moveToNext());
        }

        return users;
    }

}
