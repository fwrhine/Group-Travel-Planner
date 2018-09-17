package com.example.aghniaprawira.ppl;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Activity_GroupList extends AppCompatActivity {
    private static final String TAG = "ListGroupActivity";

    DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewGroup;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        setSupportActionBar(toolbar);
        init();

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

        populateGroupListView();

    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateGroupListView() {
        Log.d(TAG, "populateGroupListView: Displaying list of groups in the ListView.");

        //get data and append to list
        List<Group> groups = databaseHelper.getAllGroups();
        List<String> group_names = new ArrayList<>();

        for (Group group: groups) {
            group_names.add(group.getGroup_name());
        }

//        CustomList listAdapter = new CustomList(this, groups);
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, group_names);
        listViewGroup.setAdapter(adapter);
//        listViewGroup=(ListView)findViewById(R.id.listViewGroup);
//        listViewGroup.setAdapter(listAdapter);
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewGroup = (RecyclerView)findViewById(R.id.rv);
        databaseHelper = new DatabaseHelper(this);
        fab = findViewById(R.id.fab);
    }

}
