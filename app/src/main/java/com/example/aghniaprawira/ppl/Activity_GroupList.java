package com.example.aghniaprawira.ppl;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Activity_GroupList extends AppCompatActivity {
    private static final String TAG = "ListGroupActivity";

    DatabaseHelper databaseHelper;
    private ListView listViewGroup;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        setContentView(R.layout.activity_group_list);
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

        //groups on list view: when clicked, open group chat
        listViewGroup.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                Intent intent = new Intent(Activity_GroupList.this, Activity_InGroup.class);
                startActivity(intent);
            }
        });

        populateGroupListView();

    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateGroupListView() {
        Log.d(TAG, "populateGroupListView: Displaying list of groups in the ListView.");

        //get data and append to list
        Cursor data = databaseHelper.getDataGroup();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            //get value from col1 and add to arraylist
            listData.add(data.getString(1));
        }

        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, listData);
        listViewGroup.setAdapter(adapter);
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        listViewGroup = findViewById(R.id.listViewGroup);
        databaseHelper = new DatabaseHelper(this);
    }

}
