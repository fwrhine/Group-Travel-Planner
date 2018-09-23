package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

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

        final SearchView simpleSearchView = (SearchView) findViewById(R.id.search); // inititate a search view

        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                result = searchData(newText.toString());

                Log.d("QUERY", "START_SEARCH");
                ListView searchItems = (ListView) findViewById(R.id.list);
                SearchCursorAdapter adapter = new SearchCursorAdapter(getApplicationContext(), result);
                searchItems.setAdapter(adapter);
                return true;
            }
        });

    }

    public Cursor searchData(String query) {
        SQLiteDatabase db = myDb.getReadableDatabase();
        String command = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE " +
                UserContract.UserEntry.COL_USERNAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        Cursor data = db.rawQuery(command, selectionArgs);
        Log.d("DATA", query);
        Log.d("DATA", data.getCount() + ".");
        return data;
    }

    public void gotoAdd_Friend(View vw){
        Intent i = new Intent(SearchBarActivity.this, Activity_AddFriend.class);
        startActivity(i);
    }

}
