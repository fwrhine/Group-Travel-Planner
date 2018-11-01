package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;

    private TextView event_name, event_address;
    private EditText event_description, event_date, event_start_time, event_end_time;
    private ImageView type_icon;
    private ImageButton save_event;

    private SimpleDateFormat dateFormatter1, dateFormatter2;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    private String start_time;
    private String end_time;
    private Date start_date;
    private Date end_date;
    private String s_date;
    private String desc;
    private String type;
    private int event_id;
    private Date d_date;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        init();
    }

    public void init() {
        findViewById();

        databaseHelper = new DatabaseHelper(EditEventActivity.this);

        desc = event_description.getText().toString();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        event_id = bundle.getInt("event_id");
        start_date = (Date) bundle.get("start_date");
        end_date = (Date) bundle.get("end_date");
        String date = bundle.getString("date");
        String name = bundle.getString("name");
        String desc = bundle.getString("description");
        type = bundle.getString("type");
        String address = bundle.getString("address");
        start_time = getIntent().getStringExtra("time_start");
        end_time = getIntent().getStringExtra("time_end");

        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        try {
            d_date = dateFormatter2.parse(date);
            s_date = dateFormatter1.format(d_date);
            Log.d("the date", s_date);
            event_date.setText(s_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event_start_time.setText(start_time);
        event_end_time.setText(end_time);
        event_description.setText(desc);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Edit Event");

        event_name.setText(name);
        if (type != null) {
            if (type.equals("restaurants")) {
                type_icon.setImageDrawable(getDrawable(R.drawable.ic_restaurant_black));
            } else if (type.equals("attractions")) {
                type_icon.setImageDrawable(getDrawable(R.drawable.ic_sunny_black));
            }
        }

        event_address.setText(address);

        setDateField();
        setTimeField();
        setSaveEventButton();
    }

    public void findViewById() {
        toolbar = findViewById(R.id.edit_event_toolbar);
        event_name = findViewById(R.id.event_name);
        event_address = findViewById(R.id.event_address);
        event_description = findViewById(R.id.event_description);
        event_date = findViewById(R.id.event_date);
        event_start_time = findViewById(R.id.start_time);
        event_end_time = findViewById(R.id.end_time);
        type_icon = findViewById(R.id.type_icon);
        save_event = findViewById(R.id.save_event);

    }

    public void setDateField() {
        event_date.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                d_date = newDate.getTime();
                event_date.setText(dateFormatter1.format(d_date));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(start_date.getTime());
        datePickerDialog.getDatePicker().setMaxDate(end_date.getTime());
    }

    public void setTimeField() {
        event_start_time.setOnClickListener(this);
        event_end_time.setOnClickListener(this);

        String[] split_start_time = start_time.split(":");
        int hour1 = Integer.parseInt(split_start_time[0]);
        int minute1 = Integer.parseInt(split_start_time[1]);
        startTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                event_start_time.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour1, minute1, true);//Yes 24 hour time
        startTimePickerDialog.setTitle("Select Start Time");

        String[] split_end_time = end_time.split(":");
        int hour2 = Integer.parseInt(split_end_time[0]);
        int minute2 = Integer.parseInt(split_end_time[1]);
        endTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                event_end_time.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour2, minute2, true);//Yes 24 hour time
        endTimePickerDialog.setTitle("Select End Time");
    }

    private void setSaveEventButton() {
        save_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!s_date.equals(event_date.getText().toString()) ||
                        !start_time.equals(event_start_time.getText().toString()) ||
                        !end_time.equals(event_end_time.getText().toString()) ||
                        !desc.equals(event_description.getText().toString())) {
                    AlertDialog box;
                    box = updateEventDialog();
                    box.show();
                } else {
                    onBackPressed();
                }
            }
        });
    }

    public AlertDialog updateEventDialog() {
        AlertDialog myUpdateEventDialog = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Update Event Details")
                .setMessage("Do you want to save your changes?")
                .setNeutralButton("Discard", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        EditEventActivity.this.finish();
                    }

                })

                .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your saving code
                        String start_time = event_start_time.getText().toString();
                        String end_time = event_end_time.getText().toString();
                        String description = event_description.getText().toString();
                        String date = dateFormatter2.format(d_date);

                        updateEvent(date, start_time, end_time, description);

                        final Intent data = new Intent();
                        data.putExtra("start_time", start_time);
                        data.putExtra("end_time", end_time);
                        data.putExtra("description", description);
                        data.putExtra("date", date);

                        setResult(Activity.RESULT_OK, data);
                        EditEventActivity.this.finish();

                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myUpdateEventDialog;
    }

    public void updateEvent(String date, String start_time, String end_time, String desc) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if (type.equals("restaurants") || type.equals("attractions")) {

            String updateQuery = "UPDATE " + EventContract.EventEntry.TABLE_NAME + " SET " +
                    EventContract.EventEntry.COL_DATE + " = " + "\"" + date + "\", " +
                    EventContract.EventEntry.COL_TIME_START + " = " + "\"" + start_time + "\", " +
                    EventContract.EventEntry.COL_TIME_END + " = " + "\"" + end_time + "\", " +
                    EventContract.EventEntry.COL_DESCRIPTION + " = " + "\"" + desc + "\" " +
                    " WHERE " + EventContract.EventEntry._ID + " = " + event_id;
            Log.d("updateQuery", updateQuery);
            db.execSQL(updateQuery);

            db.close();
        }
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