package com.example.pplki18.grouptravelplanner;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.util.Calendar;

public class InHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHelper myDb;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Button buttonLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_home);
        init();

        navigationView.setNavigationItemSelectedListener(this);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setHeaderInfo();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
                    new Fragment_GroupList()).commit();
            navigationView.setCheckedItem(R.id.nav_group_list);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_group_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
                        new Fragment_GroupList()).commit();
                toolbar.setTitle("My Groups");
                break;
            case R.id.nav_friend_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
                        new Fragment_Friends()).commit();
                toolbar.setTitle("My Friends");
                break;
            case R.id.nav_plan_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
                        new Fragment_PlanList()).commit();
                toolbar.setTitle("My Plans");
                break;
            case R.id.nav_reminder_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
                        new Fragment_Reminder()).commit();
                toolbar.setTitle("My Notifications");
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        sessionManager = new SessionManager(getApplicationContext());

        drawer = findViewById(R.id.drawer_home);
        toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle("My Groups");
        navigationView = findViewById(R.id.nav_home);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        buttonLogout  = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sessionManager.logoutUser();
                    }
                }
        );
    }

    public void setHeaderInfo(){
        View headerView = navigationView.getHeaderView(0);

        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InHomeActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        TextView header_fullname = headerView.findViewById(R.id.user_fullname);
        TextView header_status = headerView.findViewById(R.id.user_status);

        header_fullname.setText(sessionManager.getUserDetails().get(sessionManager.KEY_FULLNAME));

        String status = null;
        if(sessionManager.isOnTrip()){
            status = "On Trip";
        }
        else{
            status = "Not On Trip";
        }
        header_status.setText("Status: " + status);
    }

    public long generalInsertNotifier(String title, String description, String destination, Integer year, Integer month,
                                      Integer day, Integer hour, Integer minute) {
        //TODO error handling, numbers must be within limit
        ContentValues event = new ContentValues();
        ContentResolver cr = getContentResolver();
        Long eventID;
        Integer m1 = month -1;
        String destinationStr = destination;
        String date = day + m1 + year + "";

        Calendar startTime = Calendar.getInstance();
//        Integer adjustedHour = hour - 1;
        startTime.set(year, m1, day, hour, minute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, m1, day, hour + 1, minute);

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

    public void deleteEventFromCalendar(long eventID){
        ContentResolver cr = getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        cr.delete(deleteUri, null, null);
        Log.v("CALENDAR DELETE", "Event deleted");
        Toast.makeText(getApplicationContext(), "Removed Event", Toast.LENGTH_SHORT).show();
    }

}
