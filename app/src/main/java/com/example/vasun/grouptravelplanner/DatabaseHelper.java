package com.example.vasun.grouptravelplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ppl2018.db";
    public static final String TABLE_NAME = "users";
    public static final String COL_0 = "user_id";
    public static final String COL_1 = "fullname";
    public static final String COL_2 = "username";
    public static final String COL_3 = "password";
    public static final String COL_4 = "email";
    public static final String COL_5 = "gender";
    public static final String COL_6 = "phone_no";
    public static final String COL_7 = "display_picture";
    public static final String COL_8 = "status";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (\n" +
                "user_id INTEGER primary key autoincrement,\n" +
                "fullname TEXT not null,\n" +
                "username TEXT unique not null,\n" +
                "password TEXT not null,\n" +
                "email TEXT unique not null,\n" +
                "gender INTEGER not null default 0,\n" +
                "phone_no INTEGER not null default 0,\n" +
                "display_picture BLOB,\n" +
                "status INTEGER not null default 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int insertData(String fullname, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("SELECT setval('user_id_seq', (SELECT max(user_id) FROM users))");

        String query = "SELECT EXISTS(SELECT * FROM " + TABLE_NAME + " WHERE username = " + username +
                " OR ) email = " + email;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0) {
            cursor.close();
            return 2;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, fullname);
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, password);
        contentValues.put(COL_4, email);
        contentValues.put(COL_5, 0);
        contentValues.put(COL_6, 0);
        contentValues.put(COL_7, 0);
        contentValues.put(COL_8, 0);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return 0;
        else
            return 1;
    }
}
