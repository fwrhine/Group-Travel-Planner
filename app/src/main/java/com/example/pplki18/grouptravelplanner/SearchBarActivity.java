package com.example.pplki18.grouptravelplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.FriendsContract;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.util.HashMap;
import java.util.Objects;

public class SearchBarActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private Cursor result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);

        myDb = new DatabaseHelper(this);

        SearchView searchView = findViewById(R.id.search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Add Friend");

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
                    result = searchData(newText, currUsername);

                    Log.d("QUERY", "START_SEARCH");
                }

                searchItems = findViewById(R.id.list);
                SearchCursorAdapter adapter = new SearchCursorAdapter(getApplicationContext(), result);
                searchItems.setAdapter(adapter);
                return true;
            }
        });
    }

    private Cursor searchData(String query, String currentUser) {
        SQLiteDatabase db = myDb.getReadableDatabase();

        String command = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE "
                + UserContract.UserEntry.COL_USERNAME + " LIKE ? AND "
                + UserContract.UserEntry.COL_USERNAME + " != ?";
        String[] selectionArgs = new String[]{"%" + query + "%", currentUser};
        Cursor data = db.rawQuery(command, selectionArgs);

        Log.d("DATA", data.getCount() + ".");
        return data;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class SearchCursorAdapter extends CursorAdapter {
        SessionManager sessionManager;

        SearchCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.search_row, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final TextView tvUsername = view.findViewById(R.id.username);
            TextView tvFullname = view.findViewById(R.id.fullname);
            ImageView tvDisplayPicture = view.findViewById(R.id.profile_image);

            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));

            Log.d("CURSOR", cursor.getString(cursor.getColumnIndexOrThrow("fullname")) + "");
            byte[] byteArray = cursor.getBlob(cursor.getColumnIndexOrThrow("display_picture"));
            Bitmap bmImage;
            if (byteArray != null) {
                bmImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                tvDisplayPicture.setImageBitmap(bmImage);
            }

            tvUsername.setText("@" + String.valueOf(username));
            tvFullname.setText(String.valueOf(fullname));

            final Button addFriendBtn = view.findViewById(R.id.add_friend);
            addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertFriends();
                    addFriendBtn.setEnabled(false);
                    addFriendBtn.setBackgroundColor(Color.parseColor("#9E9E9E"));
                }
            });
        }

        Integer findFriendId(String username1){
            Integer friendID;
            String selectQuery = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME +
                    " WHERE " + UserContract.UserEntry.COL_USERNAME + " = " + "\"" + username1 + "\"";

            Log.e("USERS", selectQuery);


            SQLiteDatabase db = myDb.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            friendID = c.getColumnIndex(UserContract.UserEntry._ID);
            // looping through all rows and adding to list
            c.close();
            return friendID;
        }

        void insertFriends(){   // Removed Parameter (N)
            myDb = new DatabaseHelper(SearchBarActivity.this);
            SQLiteDatabase db = myDb.getReadableDatabase();
            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();

            TextView friendUsername = findViewById(R.id.username);
            String friendUsername2 = friendUsername.getText().toString();
            friendUsername2 = friendUsername2.substring(1, friendUsername2.length());

            ContentValues values = new ContentValues();
            values.put(FriendsContract.FriendsEntry.COL_USER_ID, user.get(sessionManager.KEY_ID));
            values.put(FriendsContract.FriendsEntry.COL_FRIEND_ID, findFriendId(friendUsername2));
            values.put(FriendsContract.FriendsEntry.COL_FRIEND_USERNAME, friendUsername2);

            db.insert(FriendsContract.FriendsEntry.TABLE_NAME, null, values);

            Log.d("AddFriend", "Added Friend");
            Toast.makeText(SearchBarActivity.this, "Added friend", Toast.LENGTH_SHORT).show();
        }
    }

}
