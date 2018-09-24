package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.SearchCursorAdapter;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.util.HashMap;

public class SearchBarActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    Cursor result;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);

        myDb = new DatabaseHelper(this);

        SearchView searchView = (SearchView) findViewById(R.id.search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpToolbar();

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        final String currUsername = user.get(SessionManager.KEY_USERNAME);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ListView searchItems;
                if (newText.isEmpty()) {
                    result = null;
                }

                else {
                    result = searchData(newText.toString(), currUsername);

                    Log.d("QUERY", "START_SEARCH");
                }

                searchItems = (ListView) findViewById(R.id.list);
                SearchCursorAdapter adapter = new SearchCursorAdapter(getApplicationContext(), result);
                searchItems.setAdapter(adapter);
                return true;
            }
        });
    }

    public Cursor searchData(String query, String currentUser) {
        SQLiteDatabase db = myDb.getReadableDatabase();

        String command = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE "
                + UserContract.UserEntry.COL_USERNAME + " LIKE ? AND "
                + UserContract.UserEntry.COL_USERNAME + " != ?";
        String[] selectionArgs = new String[]{"%" + query + "%", currentUser};
        Cursor data = db.rawQuery(command, selectionArgs);

        Log.d("DATA", data.getCount() + ".");
        return data;
    }


    public void gotoAdd_Friend(View vw){
        Intent i = new Intent(SearchBarActivity.this, Activity_AddFriend.class);
        startActivity(i);
    }


    public void setUpToolbar() {
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SearchBarActivity.this, Activity_InHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );
    }

}
