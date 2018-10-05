package com.example.pplki18.grouptravelplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.data.PlanContract;
import com.example.pplki18.grouptravelplanner.utils.Event;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_NewPlan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateNewPlanActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateNewPlanActivity";

    DatabaseHelper databaseHelper;
    Toolbar new_plan_toolbar;
    //    RecyclerView rvNewPlan;
    TextView trip_start_label, trip_start_date, trip_end_label, trip_end_date, trip_days;
    TextView date_month_year, day;
    ImageButton button_left, button_right;
    Intent intent;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter1;
    private SimpleDateFormat dateFormatter2;
    private Date date_start;
    private Date date_end;

    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);
        findViewById();

        intent = getIntent();
        databaseHelper = new DatabaseHelper(CreateNewPlanActivity.this);

        setSupportActionBar(new_plan_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Create Plan");

//        rvNewPlan.setHasFixedSize(true);
//        linearLayoutManager = new LinearLayoutManager(CreateNewPlanActivity.this);
//        rvNewPlan.setLayoutManager(linearLayoutManager);

//        populateEventRecyclerView();
        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        setDateTimeField();
    }

    public void findViewById() {
        new_plan_toolbar = (Toolbar) findViewById(R.id.new_plan_toolbar);

//        rvNewPlan = (RecyclerView) findViewById(R.id.rvNewPlan);

        trip_start_label = (TextView) findViewById(R.id.trip_start_label);
        trip_start_date = (TextView) findViewById(R.id.trip_start_date);
        trip_end_label = (TextView) findViewById(R.id.trip_end_label);
        trip_end_date = (TextView) findViewById(R.id.trip_end_date);
        trip_days = (TextView) findViewById(R.id.trip_days);

        date_month_year = (TextView) findViewById(R.id.date_month_year);
        day = (TextView) findViewById(R.id.day);

        button_left = (ImageButton) findViewById(R.id.button_left);
        button_right = (ImageButton) findViewById(R.id.button_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    //Todo: refactor? exactly the same code as the one in CreateNewGroup
//    private void populateEventRecyclerView() {
//        Log.d(TAG, "populateEventRecyclerView: Displaying list of events in the ListView.");
//
//        //get data and append to list
//        List<Event> events = getAllEvents();
//        RVAdapter_NewPlan adapter = new RVAdapter_NewPlan(events, this.getApplicationContext());
//        rvNewPlan.setAdapter(adapter);
//    }

//    /*
//     * Get all groups
//     * */
//    public List<Event> getAllEvents() {
//        List<Event> events = new ArrayList<Event>();
//        int plan_id = intent.getIntExtra("plan_id", 0);
//
//        String selectQuery = "SELECT * FROM " + EventContract.EventEntry.TABLE_NAME +
//                " WHERE " + PlanContract.PlanEntry._ID + " = " + plan_id;
//
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//
//        // looping through all rows and adding to list
//        if (c.moveToFirst()) {
//            do {
//                String title = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TITLE));
//                String time_start = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TIME_START));
//                String time_end = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TIME_END));
//                String date = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DATE));
//                String type = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TYPE));
//
//                try {
//                    Date time1 = format.parse(time_start);
//                    Date time2 = format.parse(time_end);
//                    Event event = new Event(title, time1, time2, date, type);
//
//                    String total_time = event.getTotal_time();
//
////                List<byte[]> memberPics = getAllGroupMemberPic(c.getString(c.getColumnIndex(UserContract.UserEntry.COL_PICTURE)));
////                group.setGroup_memberPics(memberPics);
//
//                    // adding to group list
//                    events.add(event);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } while (c.moveToNext());
//        }
//
//        try {
//            Event event = new Event();
//            event.setTitle("Sample Event 1");
//            event.setTime_start(format.parse("08:00"));
//            event.setTime_end(format.parse("10:00"));
//            event.setDate("4 October 2018");
//            event.setType("others");
//
//            events.add(event);
//
//            event = new Event();
//            event.setTitle("Sample Event 2");
//            event.setTime_start(format.parse("11:15"));
//            event.setTime_end(format.parse("13:30"));
//            event.setDate("4 October 2018");
//            event.setType("others");
//
//            events.add(event);
//
//            event = new Event();
//            event.setTitle("Sample Event 2");
//            event.setTime_start(format.parse("11:15"));
//            event.setTime_end(format.parse("13:30"));
//            event.setDate("4 October 2018");
//            event.setType("others");
//
//            events.add(event);
//
//            event = new Event();
//            event.setTitle("Sample Event 2");
//            event.setTime_start(format.parse("11:15"));
//            event.setTime_end(format.parse("13:30"));
//            event.setDate("4 October 2018");
//            event.setType("others");
//
//            events.add(event);
//
//            event = new Event();
//            event.setTitle("Sample Event 2");
//            event.setTime_start(format.parse("11:15"));
//            event.setTime_end(format.parse("13:30"));
//            event.setDate("4 October 2018");
//            event.setType("others");
//
//            events.add(event);
//
//            event = new Event();
//            event.setTitle("Sample Event 2");
//            event.setTime_start(format.parse("11:15"));
//            event.setTime_end(format.parse("13:30"));
//            event.setDate("4 October 2018");
//            event.setType("others");
//
//            events.add(event);
//
//            event = new Event();
//            event.setTitle("Sample Event 2");
//            event.setTime_start(format.parse("11:15"));
//            event.setTime_end(format.parse("13:30"));
//            event.setDate("4 October 2018");
//            event.setType("others");
//
//            events.add(event);
//
//            event = new Event();
//            event.setTitle("Sample Event 2");
//            event.setTime_start(format.parse("11:15"));
//            event.setTime_end(format.parse("13:30"));
//            event.setDate("4 October 2018");
//            event.setType("others");
//
//            events.add(event);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return events;
//    }

    private void setDateTimeField() {
        trip_start_date.setOnClickListener(this);
        trip_end_date.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_start = newDate.getTime();
                trip_start_date.setText(dateFormatter1.format(date_start));
                trip_start_date.setTextColor(getResources().getColor(R.color.colorBlack));

                if (!trip_start_date.getText().toString().equals("Date") &&
                        !trip_end_date.getText().toString().equals("Date")) {
                    try {
                        long diff = date_end.getTime() - date_start.getTime();
                        long total_days = diff / (24 * 60 * 60 * 1000) + 2;
                        trip_days.setText(total_days + "");
                        intent.putExtra("date", date_start);
                        setDateChanger();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                                new Fragment_EventList()).commit();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_end = newDate.getTime();
                trip_end_date.setText(dateFormatter1.format(date_end));
                trip_end_date.setTextColor(getResources().getColor(R.color.colorBlack));

                Log.d("testtttt", (!trip_start_date.getText().toString().equals("Date") &&
                        !trip_end_date.getText().toString().equals("Date")) + "");
                if (!trip_start_date.getText().toString().equals("Date") &&
                        !trip_end_date.getText().toString().equals("Date")) {
                    try {
                        long diff = date_end.getTime() - date_start.getTime();
                        long total_days = diff / (24 * 60 * 60 * 1000) + 1;
                        trip_days.setText(total_days + "");
                        intent.putExtra("date", date_start);
                        setDateChanger();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                                new Fragment_EventList()).commit();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == trip_start_date) {
            fromDatePickerDialog.show();
        } else if(view == trip_end_date) {
            toDatePickerDialog.show();
        }
    }

    public void setDateChanger() throws ParseException {
        date_month_year.setText(dateFormatter2.format(date_start));
        date_month_year.setTextColor(getResources().getColor(R.color.colorBlack));
        day.setText(new SimpleDateFormat("EEEE").format(date_start));
        day.setTextColor(getResources().getColor(R.color.colorBlack));

        button_left.setClickable(false);
        button_right.setClickable(true);

        Date start_pin = dateFormatter2.parse(dateFormatter2.format(date_start));
        final Calendar c_start_pin = Calendar.getInstance();
        c_start_pin.setTime(start_pin);
        Date end_pin = dateFormatter2.parse(dateFormatter2.format(date_end));
        final Calendar c_end_pin = Calendar.getInstance();
        c_end_pin.setTime(end_pin);

        Date cur_date = dateFormatter2.parse(date_month_year.getText().toString());
        final Calendar c_cur_date = Calendar.getInstance();
        c_cur_date.setTime(cur_date);

        button_left.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean test = c_cur_date.getTime().getTime() == c_start_pin.getTime().getTime();
                        Log.d("CUR", c_cur_date.getTime().getTime() + "");
                        Log.d("PIN", c_start_pin.getTime().getTime() + "");
                        if (c_cur_date.getTime().getTime() == c_start_pin.getTime().getTime()) {
                            button_left.setClickable(false);
//                            button_left.setColorFilter(getResources().getColor(R.color.background_color2));
                            button_right.setClickable(true);
//                            button_right.setColorFilter(getResources().getColor(R.color.colorPrimary));
                        } else {
                            button_right.setClickable(true);
//                            button_right.setColorFilter(getResources().getColor(R.color.colorPrimary));
                            c_cur_date.add(Calendar.DATE, -1);
                            date_month_year.setText(dateFormatter2.format(c_cur_date.getTime()));
                            day.setText(new SimpleDateFormat("EEEE").format(c_cur_date.getTime()));
                        }
                        intent.putExtra("date", c_cur_date.getTime());
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                                new Fragment_EventList()).commit();
                    }
                }
        );

        button_right.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (c_cur_date.getTime().getTime() == c_end_pin.getTime().getTime()) {
                            button_left.setClickable(true);
//                            button_left.setColorFilter(getResources().getColor(R.color.colorPrimary));
                            button_right.setClickable(false);
//                            button_right.setColorFilter(getResources().getColor(R.color.background_color2));
                        } else {
                            button_left.setClickable(true);
//                            button_left.setColorFilter(getResources().getColor(R.color.colorPrimary));
                            c_cur_date.add(Calendar.DATE, 1);
                            date_month_year.setText(dateFormatter2.format(c_cur_date.getTime()));
                            day.setText(new SimpleDateFormat("EEEE").format(c_cur_date.getTime()));
                        }
                        intent.putExtra("date", c_cur_date.getTime());
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                                new Fragment_EventList()).commit();
                    }
                }
        );



    }

}
