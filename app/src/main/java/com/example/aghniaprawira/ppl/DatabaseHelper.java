package com.example.aghniaprawira.ppl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //logcat tag
    private static final String LOG = "DatabaseHelper";

    //database Version
    private static final int DATABASE_VERSION = 7;

    //database name
    private static final String DATABASE_NAME = "contactsManager";

    //table names
    private static final String TABLE_GROUP = "group_table";
    private static final String TABLE_USER = "user_table";
    private static final String TABLE_GROUP_USER = "group_user_table";

    //GROUP Table - column names
    private static final String KEY_GROUP_NAME = "name";
    private static final String KEY_GROUP_IMAGE = "image";

    //USER Table - column names
    private static final String KEY_USER_NAME = "name";

    //GROUP_USER Table -column names
    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_USER_ID = "user_id";

    //Table Create Statements
    //GROUP table create statement
    private static final String CREATE_TABLE_GROUP = "CREATE TABLE "
            + TABLE_GROUP + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_GROUP_NAME
            + " TEXT," + KEY_GROUP_IMAGE + " LONGBLOB)";

    //USER table create statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER
            + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USER_NAME + " TEXT)";

    //GROUP_USER table create statement
    private static final String CREATE_TABLE_GROUP_USER = "CREATE TABLE "
            + TABLE_GROUP_USER + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_GROUP_ID + " INTEGER," + KEY_USER_ID + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create required tables
        db.execSQL(CREATE_TABLE_GROUP);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_GROUP_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_USER);

        // create new tables
        onCreate(db);
    }

    /*
     * Creating a group
     */
    public long createGroup(Group group, ArrayList<Long> user_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_NAME, group.getGroup_name());
        values.put(KEY_GROUP_IMAGE, group.getGroup_image());

        // insert row
        long group_id = db.insert(TABLE_GROUP, null, values);

        // assigning users to groups
        for (long user_id : user_ids) {
            createGroupMembers(group_id, user_id);
        }

        return group_id;
    }

    /*
     * Get all groups
     * */
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<Group>();
        String selectQuery = "SELECT * FROM " + TABLE_GROUP;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Group group = new Group();
                Log.d("apapun", (c.getBlob(c.getColumnIndex(KEY_GROUP_IMAGE))) + ".");
                group.setGroup_name((c.getString(c.getColumnIndex(KEY_GROUP_NAME))));
                group.setGroup_image(c.getBlob(c.getColumnIndex(KEY_GROUP_IMAGE)));

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
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User user = new User();
                user.setUser_name((c.getString(c.getColumnIndex(KEY_USER_NAME))));

                // adding to group list
                users.add(user);
            } while (c.moveToNext());
        }

        return users;
    }

    /*
     * Creating group members
     */
    public long createGroupMembers(long group_id, long user_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_ID, group_id);
        values.put(KEY_USER_ID, user_id);

        long id = db.insert(TABLE_GROUP_USER, null, values);

        return id;
    }



}


