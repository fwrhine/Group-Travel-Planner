package com.example.pplki18.grouptravelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.Plan;
import com.example.pplki18.grouptravelplanner.old_stuff.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditPlanActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference planRef;

    private static final String TAG = "EditPlanActivity";

    Intent intent;

    Toolbar plan_toolbar;
    RelativeLayout trip_start, trip_end;
    TextView trip_start_date, trip_end_date, trip_days;
    TextView date_month_year, day;
    ImageButton button_left, button_right, save_plan;
    FloatingActionButton fab_add_event;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter1;
    private SimpleDateFormat dateFormatter2;
    private Date date_start;
    private Date date_end;
    private Date date_start_temp;
    private Date date_end_temp;

    private List<Plan> plans;
    private String plan_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        findViewById();

        setSupportActionBar(plan_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
    }

    public void findViewById() {
        plan_toolbar = findViewById(R.id.plan_toolbar);

        trip_start = findViewById(R.id.trip_start);
        trip_end = findViewById(R.id.trip_end);

        trip_start_date = findViewById(R.id.trip_start_date);
        trip_end_date = findViewById(R.id.trip_end_date);
        trip_days = findViewById(R.id.trip_days);

        date_month_year = findViewById(R.id.date_month_year);
        day = findViewById(R.id.day);

        button_left = findViewById(R.id.button_left);
        button_right = findViewById(R.id.button_right);

        save_plan = findViewById(R.id.save_plan);

        fab_add_event = findViewById(R.id.fab_add_event);
    }

    public void init() {
        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        intent = getIntent();
        plans = intent.getParcelableArrayListExtra("plans");
        plan_id = intent.getStringExtra("plan_id");

        String from_intent_plan_name = intent.getStringExtra("plan_name");
        String from_intent_end_date = intent.getStringExtra("plan_date_end");
        String from_intent_start_date = intent.getStringExtra("plan_date_start");
        int from_intent_total_day = intent.getIntExtra("plan_total_days", -1);
        setTitle(from_intent_plan_name);

        save_plan.setImageDrawable(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));

        try {
            date_start = dateFormatter2.parse(from_intent_start_date);
            date_start_temp = date_start;
            date_end = dateFormatter2.parse(from_intent_end_date);
            date_end_temp = date_end;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        trip_start_date.setText(dateFormatter1.format(date_start));
        trip_start_date.setTextColor(getResources().getColor(R.color.colorBlack));
        trip_end_date.setText(dateFormatter1.format(date_end));
        trip_end_date.setTextColor(getResources().getColor(R.color.colorBlack));

        String days = from_intent_total_day + "";
        trip_days.setText(days);
        setDateTimeField();

        try {
            setDateChanger();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        planRef = firebaseDatabase.getReference().child("plans").child(plan_id);

        Bundle bundle = intent.getBundleExtra("bundle");

        if (bundle != null) {
            Group group = bundle.getParcelable("group");
            if (group != null && !group.getCreator_id().equals(firebaseUser.getUid())) {
                fab_add_event.setVisibility(View.GONE);
            }
        }

        setAddEventButton();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!trip_start_date.getText().toString().equals(dateFormatter1.format(date_start)) ||
                !trip_end_date.getText().toString().equals(dateFormatter1.format(date_end))) {
            AlertDialog box;
            box = exitConfirmation();
            box.show();
        } else {
            this.finish();
        }
    }

    public AlertDialog exitConfirmation() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Trip Date Changed")
                .setMessage("Do you want to change your trip dates?")
                .setNeutralButton("Discard", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        EditPlanActivity.this.finish();
                    }

                })

                .setPositiveButton("Change", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your saving code
                        if (!checkDateAvailability()) {
                            dateNotAvailableDialog();
                        } else {
                            updatePlanDate();
                            EditPlanActivity.this.finish();
                        }
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

    // TODO set the triple dot more button
    private void setMoreButton() {
        save_plan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updatePlanDate();
                    }
                }
        );
    }

    private boolean checkDateAvailability() {
        Date start_date = null;
        Date end_date = null;
        try {
            start_date = dateFormatter2.parse(dateFormatter2.format(date_start));
            end_date = dateFormatter2.parse(dateFormatter2.format(date_end));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Plan p : plans) {
            try {
                Date p_start = dateFormatter2.parse(p.getPlan_start_date());
                Date p_end = dateFormatter2.parse(p.getPlan_end_date());

                if ((start_date.getTime() <= p_start.getTime() & end_date.getTime() >= p_end.getTime()) ||
                        (start_date.getTime() >= p_start.getTime() & end_date.getTime() <= p_end.getTime())) {
                    return false;
                }
                if ((start_date.getTime() >= p_start.getTime() & start_date.getTime() <= p_end.getTime()) ||
                        (end_date.getTime() >= p_start.getTime() & end_date.getTime() <= p_end.getTime())) {
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void dateNotAvailableDialog() {
        String message = "You already have a plan from " + trip_start_date.getText().toString() +
                " until " + trip_end_date.getText().toString() + "\nPlease select another date!";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Date Not Available")
                .setMessage(message)
                .setCancelable(true)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updatePlanDate() {
        String start_day = dateFormatter2.format(date_start_temp);
        String end_day = dateFormatter2.format(date_end_temp);
        int total_days = Integer.parseInt(trip_days.getText().toString());

        planRef.child("plan_start_date").setValue(start_day);
        planRef.child("plan_end_date").setValue(end_day);
        planRef.child("total_days").setValue(total_days);

    }

    private void setAddEventButton() {
        fab_add_event.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date = date_month_year.getText().toString();
                        Intent myIntent = new Intent(EditPlanActivity.this, ChooseEventActivity.class);

                        myIntent.putExtra("ACTIVITY", "EditPlanActivity");
                        myIntent.putExtra("plan_id", plan_id);
                        myIntent.putExtra("date", date);
                        myIntent.putExtra("date_start", date_start);
                        myIntent.putExtra("date_end", date_end);

                        EditPlanActivity.this.startActivity(myIntent);
                        Toast.makeText(EditPlanActivity.this, "Add Event", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        if(view == trip_start) {
            fromDatePickerDialog.show();
        } else if(view == trip_end) {
            toDatePickerDialog.show();
        }
    }

    public void setDateTimeField() {
        trip_start.setOnClickListener(this);
        trip_end.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_start_temp = newDate.getTime();
                trip_start_date.setText(dateFormatter1.format(date_start_temp));
                trip_start_date.setTextColor(getResources().getColor(R.color.colorBlack));

                try {
                    long diff = date_end_temp.getTime() - date_start_temp.getTime();
                    long total_days = diff / (24 * 60 * 60 * 1000) + 2;
                    trip_days.setText(total_days + "");
                    intent.putExtra("date", date_start_temp);
                    putExtraPlanDateRange();
                    setDateChanger();
                    beginFragmentEventList();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_end_temp = newDate.getTime();
                trip_end_date.setText(dateFormatter1.format(date_end_temp));
                trip_end_date.setTextColor(getResources().getColor(R.color.colorBlack));

                try {
                    long diff = date_end_temp.getTime() - date_start_temp.getTime();
                    long total_days = diff / (24 * 60 * 60 * 1000) + 1;
                    trip_days.setText(total_days + "");
                    intent.putExtra("date", date_start_temp);
                    putExtraPlanDateRange();
                    setDateChanger();
                    beginFragmentEventList();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
        toDatePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
    }

    public void setDateChanger() throws ParseException {
        button_left.setEnabled(false);
        date_month_year.setText(dateFormatter2.format(date_start_temp));
        date_month_year.setTextColor(getResources().getColor(R.color.colorBlack));
        day.setText(new SimpleDateFormat("EEEE", Locale.US).format(date_start_temp));
        day.setTextColor(getResources().getColor(R.color.colorBlack));

        Date start_pin = dateFormatter2.parse(dateFormatter2.format(date_start_temp));
        final Calendar c_start_pin = Calendar.getInstance();
        c_start_pin.setTime(start_pin);

        Date end_pin = dateFormatter2.parse(dateFormatter2.format(date_end_temp));
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

        intent.putExtra("date", c_cur_date.getTime());
        putExtraPlanDateRange();
        beginFragmentEventList();

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
                new Fragment_EventList()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .show(new Fragment_EventList()).commit();
    }
}
