package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EDIT_EVENT = 1;

    private ImageView image, desc_icon, ic_transport, ic_money;
    private TextView title, transport, origin, destination, money;
    private TextView eventDate, eventTime, eventDuration, eventDescription;
    private ImageButton editEvent;
    private Toolbar toolbar;
    private RelativeLayout constraintLayout;
    private FloatingActionButton ic_event;
    private Event event;
    private Intent intent;
    private String type;
    private SimpleDateFormat dateFormatter2, dateFormatter3;
    private ArrayList<Event> events;
    private String prevActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_EDIT_EVENT) {
            if (resultCode == Activity.RESULT_OK) {
                String description = data.getStringExtra("description");
                eventDescription.setText(description);

                String prevActivity = data.getStringExtra("PREV_ACTIVITY");

                if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
                    ArrayList<Event> events = data.getParcelableArrayListExtra("events");
                    getIntent().putParcelableArrayListExtra("events", events);
                    getIntent().putExtra("ACTIVITY", prevActivity);
                    getIntent().putExtra("test", "ini test");
                    Log.d("TEST", "ini test");

                    setResult(RESULT_OK, getIntent());
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void init() {
        findViewById();
        intent = getIntent();
        event = intent.getParcelableExtra("event");
        type = event.getType();

        prevActivity = getIntent().getStringExtra("PREV_ACTIVITY");
        if (prevActivity != null && (prevActivity.equals("CreateNewPlanActivity"))) {
            events = getIntent().getParcelableArrayListExtra("events");
        }

        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        dateFormatter3 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = "";
        try {
            if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
                date = event.getDate();
            } else {
                date = dateFormatter2.format(dateFormatter3.parse(event.getDate()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String start = event.getTime_start();
        String end = event.getTime_end();
        String duration = event.getTotal_time();

        String timeStr = start + " - " + end;

        title.setText(event.getTitle());
        eventDate.setText(date);
        eventTime.setText(timeStr);
        eventDuration.setText(duration);
        eventDescription.setText(event.getDescription());
        transport.setText(event.getTransport_number());
        origin.setText(event.getOrigin());
        destination.setText(event.getDestination());
        money.setText(event.getPrice());

        if (type.equals("flights")) {
            initFlight();
        } else if (type.equals("trains")) {
            initTrain();
        } else if (type.equals("custom")) {
            initCustom();

        }

        setEditEventButton();
    }

    public void initFlight() {
        image.setImageResource(R.drawable.airplane_flying);
    }

    public void initTrain() {
        image.setImageResource(R.drawable.train);
        ic_event.setImageResource(R.drawable.ic_train);
        ic_transport.setImageResource(R.drawable.ic_train_black);
    }

    //TODO custom event
    public void initCustom() {
        image.setImageResource(R.drawable.notes_icon);
        ic_event.setImageResource(R.drawable.ic_event_note);
        constraintLayout.setVisibility(View.GONE);
    }

    public void setEditEventButton() {
        editEvent.setColorFilter(R.color.colorRipple);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailActivity.this, EditEventActivity.class);
                intent.putExtra("event", event);
                intent.putExtra("type", event.getType());

                Date date_start = (Date) getIntent().getExtras().get("start_date");
                Date date_end = (Date) getIntent().getExtras().get("end_date");
                intent.putExtra("start_date", date_start);
                intent.putExtra("end_date", date_end);

                if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
                    intent.putExtra("PREV_ACTIVITY", prevActivity);
                    intent.putParcelableArrayListExtra("events", events);
                    intent.putExtra("index", getIntent().getIntExtra("index", -1));
                }

                Toast.makeText(EventDetailActivity.this, "edit event", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT);
            }
        });
    }

    public void findViewById() {
        toolbar = findViewById(R.id.toolbar);
        image = findViewById(R.id.image);
        desc_icon = findViewById(R.id.ic_desc);
        ic_transport = findViewById(R.id.ic_transport);
        ic_money = findViewById(R.id.ic_money);
        ic_event = findViewById(R.id.ic_event);
        title = findViewById(R.id.title);
        transport = findViewById(R.id.transport);
        origin = findViewById(R.id.origin);
        destination = findViewById(R.id.destination);
        money = findViewById(R.id.money);

        constraintLayout = findViewById(R.id.constraintLayout);

        eventDate = (TextView) findViewById(R.id.event_detail_date);
        eventTime = (TextView) findViewById(R.id.event_detail_time);
        eventDuration = (TextView) findViewById(R.id.event_detail_duration);
        eventDescription = (TextView) findViewById(R.id.event_detail_desc);
        editEvent = (ImageButton) findViewById(R.id.edit_event);

    }
}
