package com.example.pplki18.grouptravelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.old_stuff.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.Event;
import com.example.pplki18.grouptravelplanner.data.Plan;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateNewPlanActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateNewPlanActivity";

    DatabaseHelper databaseHelper;
    Toolbar plan_toolbar;

    TextView trip_start_date, trip_end_date, trip_days;
    TextView date_month_year, day;
    ImageButton button_left, button_right, add_event, save_plan;
    FloatingActionButton fab_add_event;
    ViewGroup parent;
    Intent intent;
    private SessionManager session;
    private HashMap<String, String> user;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter1;
    private SimpleDateFormat dateFormatter2;
    private Date date_start;
    private Date date_end;
    private String plan_name;

    private List<Event> events;
    private List<String> planIDs = new ArrayList<>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 5) {
            if(resultCode == RESULT_OK) {
                events = data.getParcelableArrayListExtra("events");

                getIntent().putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);
                getIntent().putExtra("ACTIVITY", "CreateNewPlanActivity");
                beginFragmentEventList();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);
        findViewById();

        setSupportActionBar(plan_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Create Plan");

        init();
    }

    public void init() {
        events = new ArrayList<>();

        intent = getIntent();

        databaseHelper = new DatabaseHelper(CreateNewPlanActivity.this);
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

//        add_event.setVisibility(View.GONE);
        setAddEventButton();
        setSavePlanButton();
        setDateTimeField();
    }

    public void findViewById() {
        plan_toolbar = (Toolbar) findViewById(R.id.plan_toolbar);

        trip_start_date = (TextView) findViewById(R.id.trip_start_date);
        trip_end_date = (TextView) findViewById(R.id.trip_end_date);
        trip_days = (TextView) findViewById(R.id.trip_days);

        date_month_year = (TextView) findViewById(R.id.date_month_year);
        day = (TextView) findViewById(R.id.day);

        button_left = (ImageButton) findViewById(R.id.button_left);
        button_right = (ImageButton) findViewById(R.id.button_right);
//        add_event = (ImageButton) findViewById(R.id.add_event);
        save_plan = (ImageButton) findViewById(R.id.save_plan);

        fab_add_event = (FloatingActionButton) findViewById(R.id.fab_add_event);

        parent = (ViewGroup) findViewById(R.id.container);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (!trip_start_date.getText().toString().equals("Date") ||
                !trip_end_date.getText().toString().equals("Date")) {
            AlertDialog box;
            box = exitConfirmation();
            box.show();
        } else {
            super.onBackPressed();
        }
    }

    public AlertDialog exitConfirmation() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Unsaved Changes")
                .setMessage("Do you want to discard your new plan?")
                .setNeutralButton("Discard", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        CreateNewPlanActivity.this.finish();
                    }

                })

                .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your saving code
                        savePlanDialog();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void setSavePlanButton() {
        save_plan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        savePlanDialog();
                    }
                }
        );
    }

    private void savePlanDialog() {
        if (trip_start_date.getText().toString().equals("Date") ||
                trip_end_date.getText().toString().equals("Date")) {
            saveAlertDialog();
        } else {
            askPlanNameDialog();
        }
    }

    private void saveAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Missing required info")
                .setMessage("Trip start date is required.\nTrip end date is required.")
                .setCancelable(true)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void askPlanNameDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View linearLayout = factory.inflate(R.layout.save_plan_dialog, parent, false);

        final EditText edtTextName = (EditText) linearLayout.findViewById(R.id.editText_planName);
        plan_name = intent.getStringExtra("plan_name");
        edtTextName.setText(plan_name);

        final EditText edtTextDesc = (EditText) linearLayout.findViewById(R.id.editText_planDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Set your plan name and description!")
                .setCancelable(false)
                .setIcon(R.drawable.ic_save_black_24dp)
                .setView(linearLayout)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your saving code
                        plan_name = edtTextName.getText().toString();
                        String plan_desc = edtTextDesc.getText().toString();
                        savePlanToDB(plan_name, plan_desc);
                        CreateNewPlanActivity.this.finish();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void savePlanToDB(final String plan_name_fix, final String plan_desc) {
        savePlanHelper(plan_name_fix, plan_desc, new InsertPlanCallback() {
            @Override
            public void onCallback(String planId) {
                savePlanToUser(planId);
                saveEventToDB(planId);
                Log.d("PLAN_ID", planId);
            }
        });
    }

    private void savePlanHelper(final String plan_name_fix, final String plan_desc, final InsertPlanCallback callback){
        final String start_day = dateFormatter2.format(date_start);
        final String end_day = dateFormatter2.format(date_end);
        final int total_days = Integer.parseInt(trip_days.getText().toString());

        final DatabaseReference planRef = firebaseDatabase.getReference().child("plans");

        final String planId = planRef.push().getKey();
        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Plan plan = new Plan(planId, plan_name_fix, mAuth.getCurrentUser().getUid());
                plan.setPlan_start_date(start_day);
                plan.setPlan_end_date(end_day);
                plan.setPlan_total_days(total_days);
                plan.setPlan_overview(plan_desc);
                planRef.child(planId).setValue(plan);
                callback.onCallback(planId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void savePlanToUser(final String newPlanID){
        final DatabaseReference userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        getAllPlanIDs(new PlanIdCallback() {
            @Override
            public void onCallback(final List<String> planID) {
                userRef.child("plans").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        planID.add(newPlanID);
                        userRef.child("plans").setValue(planID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void getAllPlanIDs(final PlanIdCallback callback){
        final DatabaseReference userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        userRef.child("plans").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                planIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String planId = postSnapshot.getValue(String.class); // String of groupID
                    planIDs.add(planId);
                }
                callback.onCallback(planIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveEventToDB(String plan_id) {
        final DatabaseReference planRef = firebaseDatabase.getReference().child("plans").child(plan_id).child("events");
        final List<String> eventIDs = getEventIDs(planRef, plan_id);
        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                planRef.setValue(eventIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference eventRef = firebaseDatabase.getReference().child("events");
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (Event e : events){
                    eventRef.child(e.getEvent_id()).setValue(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private List<String> getEventIDs(DatabaseReference eventRef, String planID){

        List<String> eventIDs = new ArrayList<>();
        for (Event e : events){
            e.setCreator_id(firebaseUser.getUid());
            e.setEvent_id(eventRef.push().getKey());
            e.setPlan_id(planID);
            eventIDs.add(e.getEvent_id());
        }
        return eventIDs;
    }

    private void setAddEventButton() {
        fab_add_event.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (trip_start_date.getText().toString().equals("Date") ||
                                trip_end_date.getText().toString().equals("Date")) {
                            saveAlertDialog();
                        } else {
                            String date = date_month_year.getText().toString();
                            Intent myIntent = new Intent(CreateNewPlanActivity.this, ChooseEventActivity.class);

                            myIntent.putExtra("ACTIVITY", "CreateNewPlanActivity");
                            myIntent.putExtra("plan_name", plan_name);
                            myIntent.putExtra("date", date);
                            myIntent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

//                            CreateNewPlanActivity.this.startActivity(myIntent);
                            startActivityForResult(myIntent, 1);
                            Toast.makeText(CreateNewPlanActivity.this, "Add Event", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void setDateTimeField() {
        trip_start_date.setOnClickListener(this);
        trip_end_date.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_start = newDate.getTime();
                trip_start_date.setText(dateFormatter1.format(date_start));
                trip_start_date.setTextColor(getResources().getColor(R.color.colorBlack));

                if (!trip_start_date.getText().toString().equals("Date") &&
                        !trip_end_date.getText().toString().equals("Date")) {
                    try {
                        long diff = date_end.getTime() - date_start.getTime();
                        long total_days = diff / (24 * 60 * 60 * 1000) + 2;
                        trip_days.setText(total_days + "");
                        intent.putExtra("date", date_start);
                        putExtraPlanDateRange();
                        setDateChanger();
                        beginFragmentEventList();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_end = newDate.getTime();
                trip_end_date.setText(dateFormatter1.format(date_end));
                trip_end_date.setTextColor(getResources().getColor(R.color.colorBlack));

                if (!trip_start_date.getText().toString().equals("Date") &&
                        !trip_end_date.getText().toString().equals("Date")) {
                    try {
                        long diff = date_end.getTime() - date_start.getTime();
                        long total_days = diff / (24 * 60 * 60 * 1000) + 1;
                        trip_days.setText(total_days + "");
                        intent.putExtra("date", date_start);
                        putExtraPlanDateRange();
                        setDateChanger();
                        beginFragmentEventList();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
        toDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
    }

    @Override
    public void onClick(View view) {
        if(view == trip_start_date) {
            fromDatePickerDialog.show();
        } else if(view == trip_end_date) {
            toDatePickerDialog.show();
        }
    }

    public void setDateChanger() throws ParseException {
        button_left.setEnabled(false);
        date_month_year.setText(dateFormatter2.format(date_start));
        date_month_year.setTextColor(getResources().getColor(R.color.colorBlack));
        day.setText(new SimpleDateFormat("EEEE", Locale.US).format(date_start));
        day.setTextColor(getResources().getColor(R.color.colorBlack));

        Date start_pin = dateFormatter2.parse(dateFormatter2.format(date_start));
        final Calendar c_start_pin = Calendar.getInstance();
        c_start_pin.setTime(start_pin);

        Date end_pin = dateFormatter2.parse(dateFormatter2.format(date_end));
        final Calendar c_end_pin = Calendar.getInstance();
        c_end_pin.setTime(end_pin);

        Date cur_date = dateFormatter2.parse(date_month_year.getText().toString());
        final Calendar c_cur_date = Calendar.getInstance();
        c_cur_date.setTime(cur_date);

        if (c_cur_date.getTime().getTime() != c_end_pin.getTime().getTime()) {
            button_right.setEnabled(true);
            button_right.setBackgroundResource(R.drawable.ripple_oval);
        } else {
            button_right.setEnabled(false);
            button_right.setBackgroundResource(R.drawable.ripple_oval_off);
            button_left.setEnabled(false);
            button_left.setBackgroundResource(R.drawable.ripple_oval_off);
        }

        button_left.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        c_cur_date.add(Calendar.DATE, -1);
                        date_month_year.setText(dateFormatter2.format(c_cur_date.getTime()));
                        day.setText(new SimpleDateFormat("EEEE", Locale.US).format(c_cur_date.getTime()));

                        if (c_cur_date.getTime().getTime() == c_start_pin.getTime().getTime()) {
                            button_left.setEnabled(false);
                            button_left.setBackgroundResource(R.drawable.ripple_oval_off);
                            button_right.setEnabled(true);
                            button_right.setBackgroundResource(R.drawable.ripple_oval);
                        } else {
                            button_right.setEnabled(true);
                            button_right.setBackgroundResource(R.drawable.ripple_oval);
                            button_left.setBackgroundResource(R.drawable.ripple_oval);
                        }

                        intent.putExtra("date", c_cur_date.getTime());
                        putExtraPlanDateRange();
                        beginFragmentEventList();
                    }
                }
        );

        button_right.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        c_cur_date.add(Calendar.DATE, 1);
                        date_month_year.setText(dateFormatter2.format(c_cur_date.getTime()));
                        day.setText(new SimpleDateFormat("EEEE", Locale.US).format(c_cur_date.getTime()));

                        if (c_cur_date.getTime().getTime() == c_end_pin.getTime().getTime()) {
                            button_left.setEnabled(true);
                            button_left.setBackgroundResource(R.drawable.ripple_oval);
                            button_right.setEnabled(false);
                            button_right.setBackgroundResource(R.drawable.ripple_oval_off);
                        } else {
                            button_left.setEnabled(true);
                            button_right.setBackgroundResource(R.drawable.ripple_oval);
                            button_left.setBackgroundResource(R.drawable.ripple_oval);
                        }

                        intent.putExtra("date", c_cur_date.getTime());
                        putExtraPlanDateRange();
                        beginFragmentEventList();
                    }
                }
        );
    }

    public void putExtraPlanDateRange() {
        intent.putExtra("start_date", date_start);
        intent.putExtra("end_date", date_end);
    }

    public void beginFragmentEventList() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                new Fragment_EventList()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    private interface InsertPlanCallback {
        void onCallback(String planId);
    }

    private interface PlanIdCallback {
        void onCallback(List<String> planID);
    }
}
