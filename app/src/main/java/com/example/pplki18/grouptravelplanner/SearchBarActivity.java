package com.example.pplki18.grouptravelplanner;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract;

public class SearchBarActivity extends AppCompatActivity {

    CursorAdapter adapter;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);
        myDb = new DatabaseHelper(this);
        Cursor result;

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            result = searchData(query);

            if (result.getCount() == 0) {
                Toast.makeText(SearchBarActivity.this, "No user found",
                        Toast.LENGTH_SHORT).show();
            }

            else {

            }
        }
    }

    public Cursor searchData(String query) {
        SQLiteDatabase db = myDb.getReadableDatabase();
        String command = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE " +
        UserContract.UserEntry.COL_USERNAME + "=" + query;
        Cursor data = db.rawQuery(command, null);

        return data;
    }
}
