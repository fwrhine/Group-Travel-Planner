package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.ReminderContract;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Reminder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Fragment_Reminder extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewGroup;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    List<Reminder> channelList;
    Reminder r;
    Context context;
    long i;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

//        r = new Reminder("T2", "2018/10/25", i);
//        r.setDate("2001/9/11");
//        r.setDestination("New York");
//        r.setChannel(0);
        context = getActivity().getApplicationContext();


        //FAB: when clicked, open create new group interface
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("fab", "FAB Clicked");
//                Intent myIntent = new Intent(getActivity(), Activity_CreateReminder.class);
//                Fragment_Reminder.this.startActivity(myIntent);
                // TO TEST generalInsert
                ((InHomeActivity) getActivity()).generalInsertNotifier("T1", "T2",
                        2018, 10, 30, 8, 20);
                Toast.makeText(getActivity().getApplicationContext(), "PRESSED FAB", Toast.LENGTH_SHORT).show();

            }
        });


        recyclerViewGroup.setHasFixedSize(true);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);

        populateReminderRecyclerView();
    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateReminderRecyclerView() {
        Log.d(TAG, "populateGroupRecyclerView: Displaying list of groups in the ListView.");

        //get data and append to list
        List<Reminder> reminderList = getAllReminders();
        RVAdapter_Reminder adapter = new RVAdapter_Reminder(reminderList, getActivity());
        recyclerViewGroup.setAdapter(adapter);
    }

    private List<Reminder> getAllReminders() {
        return InHomeActivity.reminderList;
    }

    public static List<Reminder> bubbleSort(List<Reminder> arr) {
        for (int i = 0; i < arr.size(); i++) {
            for (int j = 0; j < arr.size()-1-i; j++) {
                if(arr.get(j).getDate().after(arr.get(j+1).getDate()))
                {
                    Reminder temp = arr.get(j);
                    arr.set(j, arr.get(j+1));
                    arr.set(j+1, temp);
                }
            }
        }
        return arr;
    }



    private void init() {
        recyclerViewGroup = (RecyclerView) getView().findViewById(R.id.rv2);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        databaseHelper = new DatabaseHelper(getActivity());
        fab = getView().findViewById(R.id.fab2);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

}