package com.example.pplki18.grouptravelplanner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.utils.Event;

import java.text.SimpleDateFormat;

public class EventDetailActivity extends AppCompatActivity {

    TextView eventTitle, eventLocation, eventDescription, eventPhone,
            eventTime, eventDuration, eventType, eventDate;
    ImageView eventIcon;
    Toolbar event_detail_toolbar;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        init();

        setSupportActionBar(event_detail_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void findViewById() {
        eventTitle = (TextView) findViewById(R.id.event_detail_title);
        eventLocation = (TextView) findViewById(R.id.event_detail_location);
        eventDescription = (TextView) findViewById(R.id.event_detail_description);
        eventPhone = (TextView) findViewById(R.id.event_detail_phone);
        eventDuration = (TextView) findViewById(R.id.event_detail_duration);
        eventType = (TextView) findViewById(R.id.event_detail_type);
        eventTime = (TextView) findViewById(R.id.event_detail_time);
        eventDate = (TextView) findViewById(R.id.event_detail_date);
        eventIcon = (ImageView) findViewById(R.id.event_detail_icon);

        event_detail_toolbar = (Toolbar) findViewById(R.id.event_detail_toolbar);
    }

    public void init() {
        findViewById();
        databaseHelper = new DatabaseHelper(EventDetailActivity.this);
        int event_id = getIntent().getIntExtra("event_id", -1);
        Event event = getEventData(event_id);

        String title = event.getTitle();
        String location = event.getLocation();
        String description = event.getDescription();
        String phone = event.getPhone();
        String duration = event.getTotal_time();
        String startTime = event.getTime_start();
        String endTime = event.getTime_end();
        String date = event.getDate();
        String type = event.getType();

        eventTitle.setText(title);
        eventLocation.setText(location);
        eventDescription.setText(description);
        eventPhone.setText(phone);
        eventDuration.setText(duration);
        eventTime.setText(startTime + " - " + endTime);
        eventDate.setText(date);
        Log.d("TYPE", type);
        if(type.equals("restaurants")) {
            eventType.setText("Restaurant");
            Drawable dw = getResources().getDrawable(R.drawable.ic_restaurant_black);
            eventIcon.setImageDrawable(dw);
        } else if (type.equals("attractions")) {
            eventType.setText("Attraction");
            Drawable dw = getResources().getDrawable(R.drawable.ic_sunny_black);
            eventIcon.setImageDrawable(dw);
        }
    }

    public Event getEventData(long event_id) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + EventContract.EventEntry.TABLE_NAME +
                " WHERE " + EventContract.EventEntry._ID + " = " + event_id;

        Cursor c = db.rawQuery(selectQuery, null);

        Event event = null;

        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TITLE));
                String time_start = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TIME_START));
                String time_end = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TIME_END));
                String location = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_LOCATION));
                String phone = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_PHONE));
                String date = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DATE));
                String type = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_TYPE));
                String description = c.getString(c.getColumnIndex(EventContract.EventEntry.COL_DESCRIPTION));

                try {
                    event = new Event(title, time_start, time_end, type);
                    event.setDescription(description);
                    event.setEvent_id((int) event_id);
                    event.setTitle(title);
                    event.setType(type);
                    event.setLocation(location);
                    event.setPhone(phone);
                    event.setDate(date);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }

        return event;
    }
}
