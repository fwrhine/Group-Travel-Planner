package com.example.aghniaprawira.ppl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE0 = "group_table";
    private static final String TABLE0_COL1 = "name";
    private static final String TABLE0_COL2 = "image";

    private static final String TABLE1 = "user_table";
    private static final String TABLE1_COL1 = "name";


    public DatabaseHelper(Context context) {
        super(context, TABLE0, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableGroup = "CREATE TABLE " + TABLE0 + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TABLE0_COL1 + " TEXT, " +  TABLE0_COL2 + " LONGBLOB)";
        db.execSQL(createTableGroup);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String createTableUser = "CREATE TABLE " + TABLE1 + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE1_COL1 + " TEXT)";
            db.execSQL(createTableUser);

            Log.v("HERE", "here!!!");
            String insertUser = "INSERT INTO USER_TABLE VALUES (NULL, 'Aghnia')";
            db.execSQL(insertUser);

            insertUser = "INSERT INTO USER_TABLE VALUES (NULL, 'Yuris')";
            db.execSQL(insertUser);

            insertUser = "INSERT INTO USER_TABLE VALUES (NULL, 'Bayu')";
            db.execSQL(insertUser);

            insertUser = "INSERT INTO USER_TABLE VALUES (NULL, 'Nopal')";
            db.execSQL(insertUser);

            insertUser = "INSERT INTO USER_TABLE VALUES (NULL, 'Vincent')";
            db.execSQL(insertUser);
        }
        if (oldVersion < 3) {
            String updateTableGroup = "ALTER TABLE " + TABLE0 + " ADD " +
                    TABLE0_COL2 + " LONGBLOB";
            db.execSQL(updateTableGroup);
        }

    }

    public boolean addData(String name, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE0_COL1, name);
        contentValues.put(TABLE0_COL2, image);

        Log.d(TAG, "addData: Adding " + name + " to " + TABLE0);

        long result = db.insert(TABLE0, null, contentValues);


        if (result == -1) { //incorrect data
            return false;
        } else {
            return true;
        }
    }

    public Cursor getDataGroup() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE0;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getDataUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE1;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

}
