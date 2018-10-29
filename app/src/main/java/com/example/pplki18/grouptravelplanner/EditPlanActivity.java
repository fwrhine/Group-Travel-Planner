package com.example.pplki18.grouptravelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.PlanContract;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditPlanActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EditPlanActivity";

    DatabaseHelper databaseHelper;
    Intent intent;

    Toolbar plan_toolbar;
    TextView trip_start_date, trip_end_date, trip_days;
    TextView date_month_year, day;
    ImageButton button_left, button_right, add_event, save_plan;
    FloatingActionButton fab_add_event;

    private SessionManager session;
    private HashMap<String, String> user;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter1;
    private SimpleDateFormat dateFormatter2;
    private Date date_start;
    private Date date_end;
    private Date date_start_temp;
    private Date date_end_temp;

    private int plan_id;
    private String from_intent_plan_name;
    private String from_intent_end_date;
    private String from_intent_start_date;
    private int from_intent_total_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        findViewById();

        setSupportActionBar(plan_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
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
    }

    public void init() {
        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        intent = getIntent();
        plan_id = intent.getIntExtra("plan_id", -1);
        databaseHelper = new DatabaseHelper(EditPlanActivity.this);
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        from_intent_plan_name = intent.getStringExtra("plan_name");
        from_intent_end_date = intent.getStringExtra("plan_date_end");
        from_intent_start_date = intent.getStringExtra("plan_date_start");
        from_intent_total_day = intent.getIntExtra("plan_total_days", -1);
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
        trip_days.setText(from_intent_total_day + "");
        setDateTimeField();

        try {
            setDateChanger();
        } catch (ParseException e) {
            e.printStackTrace();
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
        Log.d("dateeeeee", dateFormatter1.format(date_start));
        Log.d("dateeeeee", dateFormatter1.format(date_end));
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
//                        deletePlan(plan_id);
                        EditPlanActivity.this.finish();
                    }

                })

                .setPositiveButton("Change", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your saving code
                        updatePlanDate();
                        EditPlanActivity.this.finish();
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

    private void updatePlanDate() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String start_day = dateFormatter2.format(date_start_temp);
        String end_day = dateFormatter2.format(date_end_temp);
        int total_days = Integer.parseInt(trip_days.getText().toString());
        String updateQuery = "UPDATE " + PlanContract.PlanEntry.TABLE_NAME + " SET " +
                PlanContract.PlanEntry.COL_START_DAY + " = " + "\"" + start_day + "\", " +
                PlanContract.PlanEntry.COL_END_DAY + " = " + "\"" + end_day + "\", " +
                PlanContract.PlanEntry.COL_TOTAL_DAY + " = " + total_days + " WHERE " +
                PlanContract.PlanEntry._ID + " = " + plan_id;
        Log.d("updateQuery", updateQuery);
        db.execSQL(updateQuery);
        db.close();

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
        if(view == trip_start_date) {
            fromDatePickerDialog.show();
        } else if(view == trip_end_date) {
            toDatePickerDialog.show();
        }
    }

    public void setDateTimeField() {
        trip_start_date.setOnClickListener(this);
        trip_end_date.setOnClickListener(this);

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
//                        boolean test = c_cur_date.getTime().getTime() == c_start_pin.getTime().getTime();
//                        Log.d("CUR", c_cur_date.getTime().getTime() + "");
//                        Log.d("PIN", c_start_pin.getTime().getTime() + "");

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
                        day.setText(new SimpleDateFormat("EEEE").format(c_cur_date.getTime()));

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
