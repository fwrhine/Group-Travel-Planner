package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.FriendsContract;

import java.util.ArrayList;
import java.util.List;

public class Activity_FriendList extends AppCompatActivity {
    private static final String TAG = "ListFriendActivity";

    DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewGroup;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        setSupportActionBar(toolbar);
        init();

        //FAB: when clicked, open create new group interface
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("fab","FAB Clicked");
                Intent myIntent = new Intent(Activity_FriendList.this, SearchBarActivity.class);
                Activity_FriendList.this.startActivity(myIntent);
            }
        });


        recyclerViewGroup.setHasFixedSize(true);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);

        populateFriendRecyclerView();

    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateFriendRecyclerView() {
        Log.d(TAG, "populateFriendRecyclerView: Displaying list of friends in the ListView.");

        //get data and append to list
        List<Friend> friend = null;

        RVAdapter_Friend adapter = new RVAdapter_Friend(friend);
        recyclerViewGroup.setAdapter(adapter);
    }

    /*
     * Get all groups
     * */
//    public List<Friend> getAllFriends() {
//        List<Friend> friends = new ArrayList<Friend>();
//        String selectQuery = "SELECT * FROM " + FriendsContract.FriendsEntry.TABLE_NAME;
//
//        Log.e("FRIENDS", selectQuery);
//
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (c.moveToFirst()) {
//            do {
//                Friend friend = new Friend();
//                friend.setFriend_name((c.getString(c.getColumnIndex(FriendsContract.FriendsEntry.COL_USER_ID))));
//
//                // adding to group list
//                friends.add(friend);
//            } while (c.moveToNext());
//        }
//
//        return friends;
//    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewGroup = (RecyclerView)findViewById(R.id.rv2);
        linearLayoutManager = new LinearLayoutManager(this);
        databaseHelper = new DatabaseHelper(this);
        fab = findViewById(R.id.fab);
    }

}

