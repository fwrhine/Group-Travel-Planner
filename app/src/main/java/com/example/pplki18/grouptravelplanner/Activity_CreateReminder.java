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
    Button btn_pick;
    Button btn_create;
    Button btn_goto_cal;
    Button btn_del_event;
    public TextView resultYear;
    public TextView resultMonth;
    public TextView resultDay;
    public TextView resultTime;
    EditText destination;
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
        btn_del_event = (Button) findViewById(R.id.button_del_event);
        btn_create = findViewById(R.id.button_create_notification);

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
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

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

                eventID2 = generalInsertNotifier(destText, notificationTxt, destText, yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);


//                alarmCal.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
//                startAlarm(alarmCal);
                btn_del_event.setEnabled(true);
                Toast.makeText(getApplicationContext(), "notification added", Toast.LENGTH_SHORT).show();


            }
        });
        btn_goto_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCalendar();
            }
        });

        btn_del_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventFromCalendar(eventID2);
                btn_del_event.setEnabled(false);
            }
        });
    }

    public void gotoCalendar() {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        startActivity(calIntent);

    }


    public long generalInsertNotifier(String title, String description, String destination, Integer year, Integer month,
                                      Integer day, Integer hour, Integer minute) {
        //TODO error handling, numbers must be within limit
        ContentValues event = new ContentValues();
        ContentResolver cr = getContentResolver();
        Long eventID;
        String destinationStr = destination;
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

    public void remToDatabase(Integer eventID){
        myDb = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = myDb.getReadableDatabase();
        HashMap<String, String> user = session.getUserDetails();

        ContentValues values = new ContentValues();
        values.put(ReminderContract.ReminderEntry.COL_USER_ID, user.get(session.KEY_ID));
        values.put(ReminderContract.ReminderEntry.COL_EVENT_ID, eventID);
        values.put(ReminderContract.ReminderEntry.COL_ALARM_CHANNEL, currChannel);

        db.insert(ReminderContract.ReminderEntry.TABLE_NAME, null, values);

        Log.d("Reminder", "Added Reminder");
        Toast.makeText(getApplicationContext(), "Added Reminder", Toast.LENGTH_SHORT).show();
    }


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

    private void cancelAlarm(Integer channel){
        oldestChannel = currChannel - 1;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);
        // requestCodeList size will increase to allow the setting of multiple alarms
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), oldestChannel, intent, 0);
        currChannel--;
        Objects.requireNonNull(alarmManager).cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm notification canceled", Toast.LENGTH_SHORT).show();
    }

    public void deleteEventFromCalendar(long eventID){
        ContentResolver cr = getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.delete(deleteUri, null, null);
        Log.v("CALENDAR DELETE", "Event deleted");
        Toast.makeText(getApplicationContext(), "Removed Event", Toast.LENGTH_SHORT).show();
    }

    public void updateEvent(long eventID){
        ContentResolver cr = getContentResolver();
        Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.TITLE, "new title");
        event.put(CalendarContract.Events.DESCRIPTION, "My cool event!");
        cr.update(eventUri, event, null, null);
    }


}
