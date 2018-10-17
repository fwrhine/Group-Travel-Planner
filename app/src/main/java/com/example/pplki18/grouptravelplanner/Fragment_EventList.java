package com.example.pplki18.grouptravelplanner;

import android.content.ContentValues;
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
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.data.PlanContract;
import com.example.pplki18.grouptravelplanner.data.PlanContract.PlanEntry;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.Event;
//import com.example.pplki18.grouptravelplanner.utils.Plan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_NewPlan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Plan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Fragment_EventList extends Fragment {

    private static final String TAG = "Fragment_EventList";

    DatabaseHelper databaseHelper;
    private RecyclerView rvNewPlan;
    private LinearLayoutManager linearLayoutManager;
    private Intent intent;
    private SimpleDateFormat dateFormatter;
    private List<Event> events;
    RVAdapter_NewPlan adapter;
    DatabaseHelper myDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getActivity().setTitle("Event List");
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

        Date date = (Date) intent.getExtras().get("date");
        rvNewPlan.setHasFixedSize(true);
        rvNewPlan.setLayoutManager(linearLayoutManager);

        populateEventRecyclerView(date);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        Log.d("RESUME", "masuk resume");
        super.onResume();
        Date date = (Date) intent.getExtras().get("date");
        String prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");
        if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
            Log.d("prevActivity", "bener");
            events = getAllEventsTemp(date);
        } else {
            events = getAllEvents(date);
        }
        adapter = new RVAdapter_NewPlan(events, getActivity());
        rvNewPlan.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateEventRecyclerView(Date date) {
        Log.d(TAG, "populateEventRecyclerView: Displaying list of events in the ListView.");

        //get data and append to list
        String prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");
        if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
            Log.d("prevActivity", "bener");
            events = getAllEventsTemp(date);
        } else {
            events = getAllEvents(date);
        }

        RVAdapter_NewPlan adapter = new RVAdapter_NewPlan(events, getActivity());
        rvNewPlan.setAdapter(adapter);
    }

    /*
     * Get all event with the selected date
     * */
    public List<Event> getAllEvents(Date cur_date) {
        List<Event> all_event = new ArrayList<Event>();
        int plan_id = getActivity().getIntent().getIntExtra("plan_id", 0);
        String str_cur_date = dateFormatter.format(cur_date);
        Log.d("CUR_DATE", str_cur_date);

        String selectQuery = "SELECT * FROM " + EventContract.EventEntry.TABLE_NAME +
                " WHERE " + EventContract.EventEntry.COL_PLAN_ID + " = " + plan_id + " AND " + "( " +
                EventContract.EventEntry.COL_DATE + " = " + "\"" + str_cur_date + "\"" + " OR " +
                EventContract.EventEntry.COL_DATE_CHECK_IN + " = " + "\"" + str_cur_date + "\"" + " OR " +
                EventContract.EventEntry.COL_DATE_CHECK_OUT + " = " + "\"" + str_cur_date + "\"" + " OR " +
                EventContract.EventEntry.COL_DATE_CHECK_IN + " = " + "\"" + str_cur_date + "\"" + " )";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                long event_id = c.getLong(c.getColumnIndex(EventContract.EventEntry._ID));
                String query_id = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_QUERY_ID));
                String date = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DATE));
                String title = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TITLE));
                String time_start = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TIME_START));
                String time_end = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TIME_END));
                String type = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TYPE));
                String description = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DESCRIPTION));

                try {
                    String time1 = format.format(format.parse(time_start));
                    Log.d("time1", time1);
                    String time2 = format.format(format.parse(time_end));
                    Event event = new Event(title, date, time1, time2, type);
                    event.setQuery_id(query_id);
                    event.setDescription(description);
                    event.setEvent_id((int) event_id);
//                    if (event.getDate().equals(str_cur_date)){
                    all_event.add(event);
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        Collections.sort(all_event);
        return all_event;
    }

    public List<Event> getAllEventsTemp(Date date) {
        List<Event> all_event;
        all_event = intent.getParcelableArrayListExtra("events");
        List<Event> some_event = new ArrayList<Event>();

        String str_cur_date = dateFormatter.format(date);
        for (Event e : all_event) {
            if (e.getDate().equals(str_cur_date)) {
                some_event.add(e);
            }
        }

        Collections.sort(some_event);
        return some_event;
    }

    private void init() {
        rvNewPlan = (RecyclerView) getView().findViewById(R.id.rvNewPlan);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        databaseHelper = new DatabaseHelper(this.getActivity());
        intent = getActivity().getIntent();
        dateFormatter = new SimpleDateFormat("d MMMM yyyy", Locale.US);
    }
}
