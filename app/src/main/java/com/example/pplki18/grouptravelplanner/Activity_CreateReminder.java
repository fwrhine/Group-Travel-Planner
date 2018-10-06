package com.example.pplki18.grouptravelplanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import android.text.format.DateFormat;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Optional;

import static android.Manifest.permission.WRITE_CALENDAR;

public class Activity_CreateReminder extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    Button btn_pick;
    Button btn_create;
    public TextView resultYear;
    public TextView resultMonth;
    public TextView resultDay;
    public TextView resultTime;
    EditText destination;
    public static String destText;
    String notificationTxt;
    Calendar alarmCal;
    public static List<Reminder> reminders;
    Integer currChannel;
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;


    public Activity_CreateReminder() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);
        btn_pick = (Button) findViewById(R.id.button_pick_notification);
        resultYear = (TextView) findViewById(R.id.notifaction_resultYear);
        resultMonth = (TextView) findViewById(R.id.notifaction_resultMonth);
        resultDay = (TextView) findViewById(R.id.notifaction_resultDay);
        resultTime = (TextView) findViewById(R.id.notifaction_resultTime);

        reminders = new ArrayList<Reminder>();
        // Waits for you to click the button
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts the function below
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_CreateReminder.this, Activity_CreateReminder.this,
                        year, month, day);
                datePickerDialog.show();
            }
        });
        btn_create = findViewById(R.id.button_create_notification);
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

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    insertEntry("Test", notificationTxt, dest);
//                }

                insertNoti("Test", "OCT25");
            }
        });
    }

    // Creates and displays a notification
    // Only for this class for manual notification
    // TODO make general one
    public void createNotification(String event, String detail) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.putExtra(CalendarContract.Events.TITLE, event);
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, event);
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, detail);
        Calendar startTime = Calendar.getInstance();
        startTime.set(yearFinal, monthFinal, dayFinal, hourFinal - 1, minuteFinal);
        Calendar endTime = Calendar.getInstance();
        endTime.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                startTime.getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                endTime.getTimeInMillis());
        calIntent.putExtra(CalendarContract.Events.DTSTART, 20181212);
        calIntent.putExtra(CalendarContract.Events.DTEND, 20181213);

        startActivity(calIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void insertEntry(String pTitle, String pDescription, String pLocation) {
        Log.v("calender", "entered insertEntry");
        ContentValues values = new ContentValues();
        ContentResolver mContentResolver = getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            values.put(CalendarContract.Events.CALENDAR_ID, 3);
        } else {
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
        }

        values.put(CalendarContract.Events.TITLE, pTitle);
        values.put(CalendarContract.Events.DESCRIPTION, pDescription);
        values.put(CalendarContract.Events.EVENT_LOCATION, pLocation);
        //
        Calendar startTime = Calendar.getInstance();
        startTime.set(yearFinal, monthFinal, dayFinal, hourFinal - 1, minuteFinal);
        Calendar endTime = Calendar.getInstance();
        endTime.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
//        values.put(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
//                startTime.getTimeInMillis());
//        values.put(CalendarContract.EXTRA_EVENT_END_TIME,
//                endTime.getTimeInMillis());
        Log.v("calenderYear", yearFinal + "");
        Log.v("calenderMonth", monthFinal + "");
        Log.v("calenderDay", dayFinal + "");
        String pStartTimestamp = yearFinal + monthFinal + dayFinal + "";
        //DTSTART YEAR+MONTH+DAY
        values.put(CalendarContract.Events.DTSTART, Integer.parseInt(pStartTimestamp));
        values.put(CalendarContract.Events.DTEND, Integer.parseInt(pStartTimestamp) + 1);
        values.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Time.getCurrentTimezone()); //get the Timezone
        Log.v("calenderIF", "before if");
        if (ContextCompat.checkSelfPermission(this, WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("calenderPer", "inside if");
            // Permission is not granted
            // Ask for permision
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
            Log.v("calenderPer", "after request per");
            Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
            uri.buildUpon();
            Log.v("calender", "calendar entry inserted");
        }

    }

    public void insertNoti(String title, String description) {

        ContentValues event = new ContentValues();
        ContentResolver cr = getContentResolver();
        Calendar startTime = Calendar.getInstance();
        Integer adjstedHour = hourFinal - 1;
        startTime.set(yearFinal, monthFinal, dayFinal, hourFinal - 1, minuteFinal);
        Calendar endTime = Calendar.getInstance();
        endTime.set(yearFinal, monthFinal, dayFinal, hourFinal + 1, minuteFinal);

        // TODO might not work, with all phones. ID is set to 1 for my phone, 3 is likely the other likely possibility
        event.put(CalendarContract.Events.CALENDAR_ID, 1);


        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.DESCRIPTION, description);
        event.put(CalendarContract.Events.EVENT_LOCATION, destination.getText().toString());

        event.put(CalendarContract.Events.DTSTART, startTime.getTimeInMillis());
        event.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());

        Log.v("calenderData", title);
        Log.v("calenderData", description);
        Log.v("calenderData", destination.getText().toString());
        Log.v("calenderData", startTime.getTimeInMillis() +  "");
        Log.v("calenderData", endTime.getTimeInMillis() + "");
        Log.v("calenderData", Time.getCurrentTimezone());

        event.put(CalendarContract.Events.ALL_DAY, 0);   // 0 for false, 1 for true
        event.put("eventStatus", 1); // 0 for tentative, 1 for confirmed, 2 for canceled
        event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true
        event.put(CalendarContract.Events.EVENT_TIMEZONE, Time.getCurrentTimezone());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Log.v("calenderIF", "inside if");
            Uri url = cr.insert(CalendarContract.Events.CONTENT_URI, event);
        }
        Log.v("calender", "calendar entry inserted");

    }



    public void generalCreateNotification(String event, String detail, Integer year, Integer month,
            Integer day, Integer hour, Integer minute){
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.putExtra(CalendarContract.Events.TITLE, event);
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, event);
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, detail);
        Calendar startTime = Calendar.getInstance();
        startTime.set(year, month, day, hour, minute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, hour + 1, minute);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                startTime.getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                endTime.getTimeInMillis());
        startActivity(calIntent);
    }

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

    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);
        // requestCodeList size will increase to allow the setting of multiple alarms
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), currChannel, intent, 0);
        currChannel++;
        //TODO need to get time
        Log.v("check calender", alarmCal.getTimeInMillis() + "");
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), pendingIntent );
    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);
        // requestCodeList size will increase to allow the setting of multiple alarms
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), currChannel, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm notification canceled", Toast.LENGTH_SHORT).show();
    }



}