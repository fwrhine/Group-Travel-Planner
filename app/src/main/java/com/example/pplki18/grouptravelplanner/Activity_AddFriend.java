//package com.example.pplki18.grouptravelplanner;
//
//import android.content.ContentValues;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
//import com.example.pplki18.grouptravelplanner.data.FriendsContract;
//import com.example.pplki18.grouptravelplanner.data.UserContract;
//import com.example.pplki18.grouptravelplanner.utils.SessionManager;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class Activity_AddFriend extends AppCompatActivity {
//    DatabaseHelper myDb;
//    private Button btnAdd;
//    private TextView username;
//    private Button addFriendButton;
//    private Toolbar toolbar;
//    private SessionManager sessionManager;
//    private RecyclerView recyclerViewFriend;
//    private LinearLayoutManager linearLayoutManager;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.search_row);
//        setSupportActionBar(toolbar);
//        init();
//
//    }
//
//    public Integer findFriendId(String username1){
//        Integer friendID;
//        String selectQuery = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME +
//                "WHERE " + UserContract.UserEntry.COL_USERNAME + " = " +  username1 + ";";
//
//        Log.e("USERS", selectQuery);
//
//
//        SQLiteDatabase db = myDb.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//        friendID = c.getColumnIndex(UserContract.UserEntry._ID);
//        // looping through all rows and adding to list
//        return friendID;
//    }
//
//    public void insertFriends(View addBtn){
//        SQLiteDatabase db = myDb.getReadableDatabase();
//        sessionManager = new SessionManager(getApplicationContext());
//        HashMap<String, String> user = sessionManager.getUserDetails();
//        TextView friendUsername = findViewById(R.id.username);
//        String friendUsername2 = friendUsername.getText().toString();
//        ContentValues values = new ContentValues();
//        values.put(FriendsContract.FriendsEntry.COL_USER_ID, user.get(sessionManager.KEY_USERNAME));
//        values.put(FriendsContract.FriendsEntry.COL_FRIEND_ID, findFriendId(friendUsername2));
//        values.put(FriendsContract.FriendsEntry.COL_FRIEND_USERNAME, friendUsername2);
//        db.insert(FriendsContract.FriendsEntry.TABLE_NAME, null, values);
//        Toast.makeText(this, "Added friend", Toast.LENGTH_SHORT).show();
//    }
//
//    private void init() {
//        toolbar = findViewById(R.id.toolbar);
//        addFriendButton = findViewById(R.id.add_friend);
//        username = findViewById(R.id.username);
//        myDb = new DatabaseHelper(this);
//        sessionManager = new SessionManager(getApplicationContext());
//        recyclerViewFriend = (RecyclerView) findViewById(R.id.rv2);
//        linearLayoutManager = new LinearLayoutManager(this);
//    }
//
//}
