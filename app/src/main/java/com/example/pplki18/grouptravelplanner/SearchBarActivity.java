package com.example.pplki18.grouptravelplanner;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.SearchCursorAdapter;

public class SearchBarActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    Cursor result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);
        myDb = new DatabaseHelper(this);

        // Get the intent, verify the action and get the query

        /*Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            result = searchData(query);
            */

        final SearchView simpleSearchView = (SearchView) findViewById(R.id.search); // inititate a search view

        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit
                result = searchData(query.toString());

            /*if (result.getCount() == 0) {
                Log.d("QUERY", "FAIL_SEARCH");
                Toast.makeText(SearchBarActivity.this, "No user found",
                        Toast.LENGTH_SHORT).show();
            } else {*/
                Log.d("QUERY", "START_SEARCH");
                ListView searchItems = (ListView) findViewById(R.id.list);
                SearchCursorAdapter adapter = new SearchCursorAdapter(getApplicationContext(), result);
                searchItems.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                return false;
            }
        });

    }

    public Cursor searchData(String query) {
        SQLiteDatabase db = myDb.getReadableDatabase();
        String command = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE " +
                UserContract.UserEntry.COL_USERNAME + "=?";
        String[] selectionArgs = new String[]{query};
        Cursor data = db.rawQuery(command, selectionArgs);
        Log.d("DATA", query);
        Log.d("DATA", data.getCount() + ".");
        return data;
    }

}
