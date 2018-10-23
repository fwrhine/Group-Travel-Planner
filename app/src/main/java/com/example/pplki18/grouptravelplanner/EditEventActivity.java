package com.example.pplki18.grouptravelplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;

    private TextView event_name, event_address;
    private EditText event_description, event_date, event_start_time, event_end_time;
    private ImageView type_icon;

    private SimpleDateFormat dateFormatter1, dateFormatter2;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    private String start_time;
    private String end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        init();
    }

    public void init() {
        findViewById();

        Bundle bundle = getIntent().getExtras();
        Date start_date = (Date) bundle.get("start_date");
        Date end_date = (Date) bundle.get("end_date");
        String date = bundle.getString("date");
        String name = bundle.getString("name");
        String type = bundle.getString("type");
        String address = bundle.getString("address");
        start_time = getIntent().getStringExtra("time_start");
        end_time = getIntent().getStringExtra("time_end");

        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        try {
            Date d_date = dateFormatter2.parse(date);
            String s_date = dateFormatter1.format(d_date);
            Log.d("the date", s_date);
            event_date.setText(s_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event_start_time.setText(start_time);
        event_end_time.setText(end_time);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Edit Event");

        event_name.setText(name);
        if (type.equals("restaurants")) {
            type_icon.setImageDrawable(getDrawable(R.drawable.ic_restaurant_black));
        } else if (type.equals("attractions")) {
            type_icon.setImageDrawable(getDrawable(R.drawable.ic_sunny_black));
        }

        event_address.setText(address);

        setDateField();
        setTimeField();
    }

    public void findViewById() {
        toolbar = (Toolbar) findViewById(R.id.edit_event_toolbar);
        event_name = (TextView) findViewById(R.id.event_name);
        event_address = (TextView) findViewById(R.id.event_address);
        event_description = (EditText) findViewById(R.id.event_description);
        event_date = (EditText) findViewById(R.id.event_date);
        event_start_time = (EditText) findViewById(R.id.start_time);
        event_end_time = (EditText) findViewById(R.id.end_time);
        type_icon = (ImageView) findViewById(R.id.type_icon);

    }

    public void setDateField() {
        event_date.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                event_date.setText(dateFormatter1.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
    }

    public void setTimeField() {
        event_start_time.setOnClickListener(this);
        event_end_time.setOnClickListener(this);

        Calendar mcurrentTime = Calendar.getInstance();
//        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//        Log.d("hour", hour+"");
//        int minute = mcurrentTime.get(Calendar.MINUTE);
//        Log.d("minute", minute+"");

        String[] split_start_time = start_time.split(":");
        int hour1 = Integer.parseInt(split_start_time[0]);
        int minute1 = Integer.parseInt(split_start_time[1]);
        startTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                event_start_time.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour1, minute1, true);//Yes 24 hour time
        startTimePickerDialog.setTitle("Select Start Time");

        String[] split_end_time = end_time.split(":");
        int hour2 = Integer.parseInt(split_end_time[0]);
        int minute2 = Integer.parseInt(split_end_time[1]);
        endTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                event_end_time.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour2, minute2, true);//Yes 24 hour time
        endTimePickerDialog.setTitle("Select End Time");
    }

    @Override
    public void onClick(View view) {
        if(view == event_date) {
            datePickerDialog.show();
        } else if (view == event_start_time) {
            startTimePickerDialog.show();
        } else if (view == event_end_time) {
            endTimePickerDialog.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
