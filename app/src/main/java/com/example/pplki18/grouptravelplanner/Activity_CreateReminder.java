package com.example.pplki18.grouptravelplanner;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Activity_CreateReminder extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
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

    public Activity_CreateReminder(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);
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
                // Starts the function below
                if (reminders.size() > 0 ){
                    currChannel = reminders.size();
                }
                else {
                    currChannel = 0;
                }

                // TODO message will be storing event time and location
                destination = findViewById(R.id.notification_destination);
                String dest = destination.getText().toString();
                destText = dest;
                notificationTxt = resultDay.getText().toString() + "/" + resultMonth.getText().toString() +
                        "/" + resultYear.getText().toString() + "   " + resultTime.getText().toString() +
                        " at " + dest;
//                nh.createNotification(notificationTxt, getApplicationContext());
                alarmCal.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
                startAlarm(alarmCal);

                String date = (dayFinal + "/" + (monthFinal+1) + "/" + yearFinal);
                Reminder rem = new Reminder(destination.getText().toString(), date, currChannel);
                reminders.add(rem);
                Toast.makeText(getApplicationContext(), "Alarm notification made", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Creates and displays a notification


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

}