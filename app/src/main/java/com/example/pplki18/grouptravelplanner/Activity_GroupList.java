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
import com.example.pplki18.grouptravelplanner.data.GroupContract;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.data.UserGroupContract;
import com.example.pplki18.grouptravelplanner.utils.Group;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Group;

import java.util.ArrayList;
import java.util.List;

public class Activity_GroupList extends AppCompatActivity {
    private static final String TAG = "ListGroupActivity";

    DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewGroup;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        init();

        setSupportActionBar(toolbar);

        //FAB: when clicked, open create new group interface
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("fab","FAB Clicked");
                Intent myIntent = new Intent(Activity_GroupList.this, Activity_CreateNewGroup.class);
                Activity_GroupList.this.startActivity(myIntent);
            }
        });


        recyclerViewGroup.setHasFixedSize(true);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);

        populateGroupRecyclerView();

        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_GroupList.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
        );

    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateGroupRecyclerView() {
        Log.d(TAG, "populateGroupRecyclerView: Displaying list of groups in the ListView.");

        //get data and append to list
        List<Group> groups = getAllGroups();

        RVAdapter_Group adapter = new RVAdapter_Group(groups);
        recyclerViewGroup.setAdapter(adapter);
    }

    /*
     * Get all groups
     * */
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<Group>();
        String selectQuery = "SELECT * FROM " + GroupContract.GroupEntry.TABLE_NAME;

        Log.e("GROUPS", selectQuery);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Group group = new Group();
                group.setGroup_name((c.getString(c.getColumnIndex(GroupContract.GroupEntry.COL_GROUP_NAME))));
                group.setGroup_image(c.getBlob(c.getColumnIndex(GroupContract.GroupEntry.COL_GROUP_IMAGE)));

                List<String> members = getAllGroupMember(c.getString(c.getColumnIndex(GroupContract.GroupEntry._ID)));
                group.setGroup_members(members);

                // adding to group list
                groups.add(group);
            } while (c.moveToNext());
        }

        return groups;
    }

    /*
     * Get all members of a group.
     */
    public List<String> getAllGroupMember(String group_id) {
        List<String> members = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + GroupContract.GroupEntry.TABLE_NAME + " g, "
                + UserContract.UserEntry.TABLE_NAME + " u, " + UserGroupContract.UserGroupEntry.TABLE_NAME
                + " ug WHERE g." + GroupContract.GroupEntry._ID + " = '" + group_id + "'" + " AND g."
                + GroupContract.GroupEntry._ID + " = " + "ug." + UserGroupContract.UserGroupEntry.COL_GROUP_ID
                + " AND u." + UserGroupContract.UserGroupEntry._ID + " = " + "ug."
                + UserGroupContract.UserGroupEntry.COL_USER_ID;

        Log.e("USERGROUP", selectQuery);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                members.add(c.getString(c.getColumnIndex(UserContract.UserEntry.COL_FULLNAME)));
            } while (c.moveToNext());
        }

        return members;
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewGroup = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        databaseHelper = new DatabaseHelper(this);
        fab = findViewById(R.id.fab);
    }

}
