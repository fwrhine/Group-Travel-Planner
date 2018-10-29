package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.utils.Event;

public class EventDetailActivity extends AppCompatActivity {
    ImageView image, desc_icon, ic_transport, ic_origin, ic_destination, ic_money;
    TextView title, description, transport, origin, destination, money;
    TextView eventDate, eventTime, eventDuration, eventDescription;
    ImageButton editEvent;
    FloatingActionButton ic_event;
    private Intent intent;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        init();
    }

    public void init() {
        findViewById();
        intent = getIntent();
        Event event = intent.getParcelableExtra("event");
        type = event.getType();

        if (type.equals("flights")) {
            initFlight(event);
        } else if (type.equals("trains")) {
            initTrain(event);
        } else if (type.equals("custom")) {
            initCustom(event);
        }
    }

    public void initFlight(Event event) {
        String start = event.getTime_start();
        String end = event.getTime_end();
        String duration = event.getTotal_time();

        String timeStr = start + " - " + end;

        image.setImageResource(R.drawable.airplane_flying);
        title.setText(event.getTitle());
        eventDate.setText(event.getDate());
        eventTime.setText(timeStr);
        eventDuration.setText(duration);
        eventDescription.setText(event.getDescription());

        transport.setText(event.getTransport_number());
        origin.setText(event.getOrigin());
        destination.setText(event.getDestination());
//        money.setText(event.getPrice());
    }

    public void initTrain(Event event) {
        image.setImageResource(R.drawable.train);
    }

    public void initCustom(Event event) {

    }

    public void findViewById() {
        image = findViewById(R.id.image);
        desc_icon = findViewById(R.id.ic_desc);
        ic_transport = findViewById(R.id.ic_transport);
        ic_origin = findViewById(R.id.ic_origin);
        ic_destination = findViewById(R.id.ic_destination);
        ic_money = findViewById(R.id.ic_money);
        title = findViewById(R.id.title);
        description = findViewById(R.id.event_detail_desc);
        transport = findViewById(R.id.transport);
        origin = findViewById(R.id.origin);
        destination = findViewById(R.id.destination);
        money = findViewById(R.id.money);

        eventDate = (TextView) findViewById(R.id.event_detail_date);
        eventTime = (TextView) findViewById(R.id.event_detail_time);
        eventDuration = (TextView) findViewById(R.id.event_detail_duration);
        eventDescription = (TextView) findViewById(R.id.event_detail_desc);
        editEvent = (ImageButton) findViewById(R.id.edit_event);


    }

}
