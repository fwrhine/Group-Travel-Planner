package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.FriendsContract;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.Friend;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;


public class Fragment_Friends extends Fragment {

//    private Button to_search_friend;    // TEMP - nopal

    private static final String TAG = "ListFriendActivity";

    DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewGroup;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton to_search_friend;
    private Toolbar toolbar;
    DatabaseHelper myDb;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Chat");
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        setAddFriendButton();


        recyclerViewGroup.setHasFixedSize(true);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);

        populateFriendRecyclerView();
    }

    // TEMP - nopal
    public void setAddFriendButton() {
        to_search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("TEMP","To Search Friend");
                Intent myIntent = new Intent(getActivity(), SearchBarActivity.class);
                Fragment_Friends.this.startActivity(myIntent);
            }
        });
    }


    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateFriendRecyclerView() {
        Log.d(TAG, "populateFriendRecyclerView: Displaying list of friends in the ListView.");

        //get data and append to list
        List<Friend> friendsList = getAllFriends();
        RVAdapter_Friend adapter = new RVAdapter_Friend(friendsList, getActivity());
        recyclerViewGroup.setAdapter(adapter);
    }

    /*
     * Get all groups
     * */
    public List<Friend> getAllFriends() {
        List<Friend> friendsList = new ArrayList<Friend>();
//        String selectQuery = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE " +
//                UserContract.UserEntry.TABLE_NAME + "." + UserContract.UserEntry._ID + " IN " +
//                "(SELECT " + FriendsContract.FriendsEntry.COL_FRIEND_ID +
//                " FROM " + FriendsContract.FriendsEntry.TABLE_NAME + " WHERE " +
//                FriendsContract.FriendsEntry.TABLE_NAME + "." + FriendsContract.FriendsEntry.COL_USER_ID + " = " +
//                sessionManager.getUserDetails().get(sessionManager.KEY_ID) + ")";
        String selectQuery = "SELECT * FROM " + FriendsContract.FriendsEntry.TABLE_NAME + ", " +
                UserContract.UserEntry.TABLE_NAME + " WHERE " + FriendsContract.FriendsEntry.COL_USER_ID
                + " = " + sessionManager.getUserDetails().get(sessionManager.KEY_ID);
//                + "." +
//                FriendsContract.FriendsEntry.COL_FRIEND_ID + " FROM " + FriendsContract.FriendsEntry.TABLE_NAME + " INTERSECT " +
//                "SELECT " + UserContract.UserEntry.TABLE_NAME + "." +
//                UserContract.UserEntry._ID +" FROM " + UserContract.UserEntry.TABLE_NAME;
        Log.d("FRIENDS", selectQuery);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        Log.d("Cursor_begin", c.getCount() + "");

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Friend friend = new Friend();
//                friend.setFriend_username((c.getString(c.getColumnIndex(UserContract.UserEntry.COL_USERNAME))));
                friend.setId((c.getInt(c.getColumnIndex(FriendsContract.FriendsEntry.COL_FRIEND_ID))));
                Log.d("FRIEND CONTENT", friend.getId() + "");


                // adding to group list
                friendsList.add(friend);
            } while (c.moveToNext());
        }

        return friendsList;
    }

    private void init() {
        recyclerViewGroup = (RecyclerView) getView().findViewById(R.id.rv2);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        databaseHelper = new DatabaseHelper(this.getActivity());
        to_search_friend = getView().findViewById(R.id.to_search_friend);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
    }
}
