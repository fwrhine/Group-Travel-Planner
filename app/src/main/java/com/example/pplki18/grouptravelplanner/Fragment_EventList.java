package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.utils.Event;
//import com.example.pplki18.grouptravelplanner.utils.Plan;
import com.example.pplki18.grouptravelplanner.utils.Plan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_NewPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class Fragment_EventList extends Fragment {

    private static final String TAG = "Fragment_EventList";
    private static final int EDIT_EVENT_REQUEST_CODE = 5;

    DatabaseHelper databaseHelper;
    private RecyclerView rvNewPlan;
    private ProgressBar progressBar;
    private LinearLayoutManager linearLayoutManager;
    private Intent intent;
    private SimpleDateFormat dateFormatter1;
    private SimpleDateFormat dateFormatter2;
    private List<Event> events = new ArrayList<>();
    private List<String> eventIDs = new ArrayList<>();
    RVAdapter_NewPlan adapter;
    DatabaseHelper myDb;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference planRef;
    private DatabaseReference eventRef;

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
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        planRef = firebaseDatabase.getReference().child("plans");
        eventRef = firebaseDatabase.getReference().child("events");

        Date date = (Date) intent.getExtras().get("date");
        rvNewPlan.setHasFixedSize(true);
        rvNewPlan.setLayoutManager(linearLayoutManager);

//        Date end_date = (Date) intent.getExtras().get("end_date");
//        Log.d("fragment_end_date", end_date.toString());

        populateEventRecyclerView(date);

    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        Date date = (Date) intent.getExtras().get("date");
        String prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");
        if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
            events = getAllEventsTemp(date);
            adapter = new RVAdapter_NewPlan(events, getActivity());
            rvNewPlan.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getAllEvents(date, new EventCallback() {
                @Override
                public void onCallback(List<Event> list) {
                    events = list;
                    progressBar.setVisibility(View.INVISIBLE);
                    adapter = new RVAdapter_NewPlan(events, getActivity());
                    rvNewPlan.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void populateEventRecyclerView(Date date) {
        Log.d(TAG, "populateEventRecyclerView: Displaying list of events in the ListView.");

        //get data and append to list
        String prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");
        if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
            events = getAllEventsTemp(date);
            adapter = new RVAdapter_NewPlan(events, getActivity());
            rvNewPlan.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getAllEvents(date, new EventCallback() {
                @Override
                public void onCallback(List<Event> list) {
                    events = list;
                    progressBar.setVisibility(View.INVISIBLE);
                    adapter = new RVAdapter_NewPlan(events, getActivity());
                    rvNewPlan.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void getEventIDs(final EventIdCallback callback) {
        final String plan_id = getActivity().getIntent().getStringExtra("plan_id");
        if(plan_id != null) {
            planRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    eventIDs.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Plan plan = postSnapshot.getValue(Plan.class);
                        if (plan_id.equals(plan.getPlan_id())) {
                            eventIDs = plan.getEvents();
//                            Log.d("EVENT_IDS", eventIDs.get(0));
                            break;
                        }
                    }
                    callback.onCallback(eventIDs);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            callback.onCallback(eventIDs);
        }
    }

    /*
     * Get all event with the selected date
     * */
    public void getAllEvents(final Date cur_date, final EventCallback callback) {
        //TODO: Firebase
        getEventIDs(new EventIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                if (list != null) {
                    eventIDs = list;
                    final String str_cur_date = dateFormatter1.format(cur_date);
                    // this one is to check the transport date (saved in different format)
                    String str_cur_date2 = dateFormatter2.format(cur_date);
                    Log.d("CUR_DATE", str_cur_date);
                    Log.d("CUR_DATE2", str_cur_date2);

                    eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            events.clear();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Event event = postSnapshot.getValue(Event.class); // Group Objects
                                if (eventIDs.contains(event.getEvent_id()) && str_cur_date.equals(event.getDate())) {
                                    Log.d("TRUE", event.getEvent_id() + "");
                                    events.add(event);
                                }
                            }
                            callback.onCallback(events);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    callback.onCallback(events);
                }
            }
        });

//
//        String selectQuery = "SELECT * FROM " + EventContract.EventEntry.TABLE_NAME +
//                " WHERE " + EventContract.EventEntry.COL_PLAN_ID + " = " + plan_id + " AND " + "( " +
//                EventContract.EventEntry.COL_DATE + " = " + "\"" + str_cur_date + "\"" + " OR " +
//                EventContract.EventEntry.COL_DATE_CHECK_IN + " = " + "\"" + str_cur_date + "\"" + " OR " +
//                EventContract.EventEntry.COL_DATE_CHECK_OUT + " = " + "\"" + str_cur_date + "\"" + " OR " +
//                EventContract.EventEntry.COL_DATE + " = " + "\"" + str_cur_date2 + "\"" + " OR " +
//                EventContract.EventEntry.COL_DATE_CHECK_IN + " = " + "\"" + str_cur_date2 + "\"" + " OR " +
//                EventContract.EventEntry.COL_DATE_CHECK_OUT + " = " + "\"" + str_cur_date2 + "\"" + " )";
//
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//
//        // looping through all rows and adding to list
//        if (c.moveToFirst()) {
//            do {
//                long event_id = c.getLong(c.getColumnIndex(EventContract.EventEntry._ID));
//                String type = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TYPE));
//                String date = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DATE));
//                String title = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TITLE));
//                String description = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DESCRIPTION));
//                String query_id = "";
//                String time_start = "";
//                String time_end = "";
//                String transport_num = "";
//                String origin = "";
//                String destination = "";
//                if (type.equals("restaurants") || type.equals("attractions") || type.equals("custom")) {
//                    query_id = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_QUERY_ID));
//                    time_start = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TIME_START));
//                    time_end = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TIME_END));
//                } else if (type.equals("trains") || type.equals("flights")) {
//                    query_id = "";
//                    time_start = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DEPARTURE_TIME));
//                    time_end = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_ARRIVAL_TIME));
//                    transport_num = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TRANS_NUMBER));
//                    origin = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_ORIGIN));
//                    destination = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DESTINATION));
//
//                } else {
//                    query_id = "";
//                }
//
//                try {
//                    String time1 = format.format(format.parse(time_start));
//                    Log.d("time1", time1);
//                    String time2 = format.format(format.parse(time_end));
//                    Event event = new Event(title, date, time1, time2, type);
//                    event.setQuery_id(query_id);
//                    event.setDescription(description);
//                    event.setEvent_id((int) event_id);
//                    event.setTransport_number(transport_num);
//                    event.setOrigin(origin);
//                    event.setDestination(destination);
//
//                    all_event.add(event);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } while (c.moveToNext());
//        }
//        Collections.sort(all_event);
//        for(Event e: all_event) {
//            Log.d("TYPE IS", e.getType());
//        }
//        c.close();
//        return all_event;
    }

    public List<Event> getAllEventsTemp(Date date) {
        List<Event> all_event;
        all_event = intent.getParcelableArrayListExtra("events");
        List<Event> some_event = new ArrayList<Event>();

        String str_cur_date = dateFormatter1.format(date);
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
        progressBar = getView().findViewById(R.id.progress_loader);
        dateFormatter1 = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    private interface EventIdCallback {
        void onCallback(List<String> list);
    }

    private interface EventCallback {
        void onCallback(List<Event> list);
    }
}
