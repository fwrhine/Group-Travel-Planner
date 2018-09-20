package com.example.pplki18.grouptravelplanner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {
    SessionManager session;
    DatabaseHelper myDb;

    TextView fullname_label, username_label, email_label;
    String fullname_s, username_s, email_s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        session = new SessionManager(getApplicationContext());

        fullname_label = (TextView) findViewById(R.id.fullname_label);
        username_label = (TextView) findViewById(R.id.username_label);
        email_label = (TextView) findViewById(R.id.email_label);

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get username from the session
        username_s = user.get(SessionManager.KEY_USERNAME);

        // get email from the session
        email_s = user.get(SessionManager.KEY_EMAIL);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        myDb = new DatabaseHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = myDb.getReadableDatabase();

        // Find the user from the database
        String query = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE "
                + UserContract.UserEntry.COL_USERNAME + "=?";
        String[] selectionArgs = new String[]{username_s};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Get the data (fullname only) from the database
        if(cursor !=null && cursor.moveToFirst()) {
            do {
                fullname_s = cursor.getString(cursor.getColumnIndex("fullname"));
                Log.d("EMAIL", fullname_s);
            } while (cursor.moveToNext());
        }

        fullname_label.setText(fullname_s);
        username_label.setText(username_s);
        email_label.setText(email_s);

    }

}
