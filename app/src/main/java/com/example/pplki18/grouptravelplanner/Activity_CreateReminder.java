package com.example.pplki18.grouptravelplanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;


import android.text.format.DateFormat;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.ReminderContract;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Activity_CreateReminder extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private Button btn_pick;
    private Button btn_create;
    private Button btn_goto_cal;
    private Button btn_cancel_event;
    private ImageButton btn_done;
    private TextView resultYear;
    private TextView resultMonth;
    private TextView resultDay;
    private TextView resultTime;
    private EditText destination;
    public static String destText;
    String notificationTxt;
    Calendar alarmCal;
    public static List<Reminder> reminders;
    public Integer currChannel;
    public Integer oldestChannel;
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    Integer WRITE_CALENDER;

    DatabaseHelper myDb;
    Cursor result;
    Toolbar toolbar;
    SessionManager session;

    long eventID2;



    public Activity_CreateReminder() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);
        WRITE_CALENDER = 4;
        //TODO currChannel should be by number of alarms
        currChannel = 1;
        myDb = new DatabaseHelper(this);
        session = new SessionManager(getApplicationContext());

        //=================
        btn_pick = (Button) findViewById(R.id.button_pick_notification);
        btn_goto_cal = (Button) findViewById(R.id.button_goto_cal);
        btn_cancel_event = (Button) findViewById(R.id.button_del_event);
        btn_create = findViewById(R.id.button_create_notification);
        btn_done = findViewById(R.id.btn_done_reminder);

        resultYear = (TextView) findViewById(R.id.notifaction_resultYear);
        resultMonth = (TextView) findViewById(R.id.notifaction_resultMonth);
        resultDay = (TextView) findViewById(R.id.notifaction_resultDay);
        resultTime = (TextView) findViewById(R.id.notifaction_resultTime);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, WRITE_CALENDER);
        }
        reminders = new ArrayList<>();
        // Waits for you to click the button
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts the function below
                getCalendar();
                DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_CreateReminder.this, Activity_CreateReminder.this,
                        year, month, day);
                datePickerDialog.show();
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO message will be storing event time and location
                destination = findViewById(R.id.notification_destination);
                String dest = destination.getText().toString();
                destText = dest;
                notificationTxt = resultDay.getText().toString() + "/" + resultMonth.getText().toString() +
                        "/" + resultYear.getText().toString() + "   " + resultTime.getText().toString() +
                        " at " + dest;
//                createNotification(dest, notificationTxt);
//                generalCreateNotification("event", "detail", Integer i);
                if (!dest.isEmpty() && !resultTime.getText().toString().isEmpty()) {
                    eventID2 = generalInsertNotifier(destText, destText, yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
                    btn_cancel_event.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "notification added", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please pick notification and set location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_goto_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCalendar();
            }
        });

        btn_cancel_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelLatestEventFromCalendar(eventID2);
                btn_cancel_event.setEnabled(false);
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_CreateReminder.this , InHomeActivity.class);
                myIntent.putExtra("fragment", "reminder");
                startActivity(myIntent);
            }
        });
    }

    public void gotoCalendar() {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "GTP");
        startActivity(calIntent);

    }


    public long generalInsertNotifier(String title, String destination, Integer year, Integer month,
                                      Integer day, Integer hour, Integer minute) {
        //TODO error handling, numbers must be within limit
        ContentValues event = new ContentValues();
        ContentResolver cr = getContentResolver();
        Long eventID;
        String description = "GTP";
        String date = day + month + year + "";

        Calendar startTime = Calendar.getInstance();
//        Integer adjustedHour = hour - 1;
        startTime.set(year, month, day, hour, minute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, hour + 1, minute);

        // TODO might not work, with all phones. ID is set to 1 for my phone, 3 is likely the other likely possibility
        event.put(CalendarContract.Events.CALENDAR_ID, 1);

        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.DESCRIPTION, description);
        event.put(CalendarContract.Events.EVENT_LOCATION, destination);

        event.put(CalendarContract.Events.DTSTART, startTime.getTimeInMillis());
        event.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());

        Log.v("calenderData", title);
        Log.v("calenderData", description);

        Log.v("calenderData", year +"");
        Log.v("calenderData", month + "");
        Log.v("calenderData", day + "");
        Log.v("calenderData", hour + "");
        Log.v("calenderData", minute + "");

        Log.v("calenderData", destination + " in 1 hour");
        Log.v("calenderData", startTime.getTimeInMillis() +  "");
        Log.v("calenderData", endTime.getTimeInMillis() + "");
        Log.v("calenderData", Time.getCurrentTimezone());

        event.put(CalendarContract.Events.ALL_DAY, 0);   // 0 for false, 1 for true
        event.put("eventStatus", 1); // 0 for tentative, 1 for confirmed, 2 for canceled
        event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Time.getCurrentTimezone());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Log.v("calenderIF", "inside if");
            Uri eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, event);
            eventID = Long.parseLong(eventUri.getLastPathSegment());

        }
        else {
            Uri eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, event);
            eventID = Long.parseLong(eventUri.getLastPathSegment());
        }
        Log.v("calender", "EVENT ID:  " + eventID);
        Log.v("calender", "calendar entry inserted");
//        Reminder newReminder = new Reminder(destinationStr,date, eventID, currChannel);
//        return eventID;

        startAlarm(eventID);

        //eventID2 depends on
        return eventID;
    }

//    public void CalendarReminder(){
//        ContentResolver cr = getContentResolver();
//        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(this) + "reminders");
//        values = new ContentValues();
//        values.put( "event_id", Long.parseLong(event.getLastPathSegment()));
//        values.put( "method", 1 );
//        values.put( "minutes", 10 );
//        cr.insert( REMINDERS_URI, values );
//    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        resultYear.setText(yearFinal + "");
        resultMonth.setText((monthFinal + 1) + "");
        resultDay.setText(dayFinal + "");
        resultTime.setText(hourFinal  + ":" + minuteFinal);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month;
        dayFinal = dayOfMonth;

        Calendar c = Calendar.getInstance();
        alarmCal = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        Log.d("Calender", "trying to set alarmCal");

        TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_CreateReminder.this, Activity_CreateReminder.this,
                hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    private void startAlarm(long eventID){
        // reminder insert
        String reminderUriString = "content://com.android.calendar/reminders";

        ContentValues reminderValues = new ContentValues();

        reminderValues.put("event_id", eventID);
        reminderValues.put("minutes", 60);
        // method 0 default, 1 alert, 2 email, 3 sms
        reminderValues.put("method", 1);

        Uri reminderUri = getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
        Log.v("calender", "return REMINDER:  " + Long.parseLong(reminderUri.getLastPathSegment()));
    }


    public void cancelLatestEventFromCalendar(long eventID){
        ContentResolver cr = getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.delete(deleteUri, null, null);
        Log.v("CALENDAR DELETE", "Event deleted");
        Toast.makeText(getApplicationContext(), "Removed Event", Toast.LENGTH_SHORT).show();
    }

    private void getCalendar() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }


}
