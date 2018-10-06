package com.example.pplki18.grouptravelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.PlanContract;
import com.example.pplki18.grouptravelplanner.utils.Plan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateNewPlanActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateNewPlanActivity";

    DatabaseHelper databaseHelper;
    Toolbar new_plan_toolbar;
    //    RecyclerView rvNewPlan;
    TextView trip_start_label, trip_start_date, trip_end_label, trip_end_date, trip_days;
    TextView date_month_year, day;
    ImageButton button_left, button_right, add_event, save_plan;
    Intent intent;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter1;
    private SimpleDateFormat dateFormatter2;
    private Date date_start;
    private Date date_end;
    private int plan_id;

    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);
        findViewById();

        intent = getIntent();
        plan_id = intent.getIntExtra("plan_id", -1);
        databaseHelper = new DatabaseHelper(CreateNewPlanActivity.this);

        setSupportActionBar(new_plan_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Create Plan");

//        rvNewPlan.setHasFixedSize(true);
//        linearLayoutManager = new LinearLayoutManager(CreateNewPlanActivity.this);
//        rvNewPlan.setLayoutManager(linearLayoutManager);

//        populateEventRecyclerView();
        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        setAddEventButton();
        setSavePlanButton();
        setDateTimeField();
    }

    public void findViewById() {
        new_plan_toolbar = (Toolbar) findViewById(R.id.new_plan_toolbar);

//        rvNewPlan = (RecyclerView) findViewById(R.id.rvNewPlan);

        trip_start_label = (TextView) findViewById(R.id.trip_start_label);
        trip_start_date = (TextView) findViewById(R.id.trip_start_date);
        trip_end_label = (TextView) findViewById(R.id.trip_end_label);
        trip_end_date = (TextView) findViewById(R.id.trip_end_date);
        trip_days = (TextView) findViewById(R.id.trip_days);

        date_month_year = (TextView) findViewById(R.id.date_month_year);
        day = (TextView) findViewById(R.id.day);

        button_left = (ImageButton) findViewById(R.id.button_left);
        button_right = (ImageButton) findViewById(R.id.button_right);
        add_event = (ImageButton) findViewById(R.id.add_event);
        save_plan = (ImageButton) findViewById(R.id.save_plan);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.d("BACK", "balik sini");

        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog box;
        box = deleteConfirmation(plan_id);
        box.show();
    }

    private AlertDialog deleteConfirmation(final int plan_id) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Unsaved Changes")
                .setMessage("Do you want to discard your new plan?")

                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deletePlan(plan_id);
                        CreateNewPlanActivity.this.finish();
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

    private void deletePlan(int plan_id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + PlanContract.PlanEntry.TABLE_NAME + " WHERE " +
                PlanContract.PlanEntry._ID + " = " + plan_id;

        db.execSQL(deleteQuery);
        db.close();
    }

    private void setSavePlanButton() {
        save_plan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (trip_start_date.getText().toString().equals("Date") ||
                                trip_end_date.getText().toString().equals("Date")) {
                            saveAlertDialog();
                        } else {
                            savePlan();
                            CreateNewPlanActivity.this.finish();
                        }
                    }
                }
        );
    }

    private void saveAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Date is not provided")
                .setMessage("Please fill in your trip dates first!")
                .setCancelable(true)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void savePlan() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        plan_id = intent.getIntExtra("plan_id", -1);
        String start_day = trip_start_date.getText().toString();
        String end_day = trip_end_date.getText().toString();
        int total_days = Integer.parseInt(trip_days.getText().toString());

        String updateQuery = "UPDATE " + PlanContract.PlanEntry.TABLE_NAME + " SET " +
                PlanContract.PlanEntry.COL_START_DAY + " = " + "\"" + start_day + "\", " +
                PlanContract.PlanEntry.COL_END_DAY + " = " + "\"" + end_day + "\", " +
                PlanContract.PlanEntry.COL_TOTAL_DAY + " = " + total_days + " WHERE " +
                PlanContract.PlanEntry._ID + " = " + plan_id;
        db.execSQL(updateQuery);
        db.close();
    }

    private void setAddEventButton() {
        add_event.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date = date_month_year.getText().toString();
//                        Intent myIntent = new Intent(CreateNewPlanActivity.this, Activity_ChooseEvent.class);
//                        myIntent.putExtra("plan_id", intent.getStringExtra("plan_id"));
//                        myIntent.putExtra("date", date);
//
//                        CreateNewPlanActivity.this.startActivity(myIntent);
                        Toast.makeText(CreateNewPlanActivity.this, "Add Event", Toast.LENGTH_SHORT).show();
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
                //toDatePickerDialog.getDatePicker().setMinDate(newDate.getTimeInMillis()); // trying to limit trip end date
                //picker from date start

                if (!trip_start_date.getText().toString().equals("Date") &&
                        !trip_end_date.getText().toString().equals("Date")) {
                    try {
                        long diff = date_end.getTime() - date_start.getTime();
                        long total_days = diff / (24 * 60 * 60 * 1000) + 2;
                        trip_days.setText(total_days + "");
                        intent.putExtra("date", date_start);
                        setDateChanger();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                                new Fragment_EventList()).commit();
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

                Log.d("testtttt", (!trip_start_date.getText().toString().equals("Date") &&
                        !trip_end_date.getText().toString().equals("Date")) + "");
                if (!trip_start_date.getText().toString().equals("Date") &&
                        !trip_end_date.getText().toString().equals("Date")) {
                    try {
                        long diff = date_end.getTime() - date_start.getTime();
                        long total_days = diff / (24 * 60 * 60 * 1000) + 1;
                        trip_days.setText(total_days + "");
                        intent.putExtra("date", date_start);
                        setDateChanger();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                                new Fragment_EventList()).commit();
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
        date_month_year.setText(dateFormatter2.format(date_start));
        date_month_year.setTextColor(getResources().getColor(R.color.colorBlack));
        day.setText(new SimpleDateFormat("EEEE").format(date_start));
        day.setTextColor(getResources().getColor(R.color.colorBlack));

        button_left.setClickable(false);
        button_right.setClickable(true);
        button_right.setBackgroundResource(R.drawable.ripple_oval);

        Date start_pin = dateFormatter2.parse(dateFormatter2.format(date_start));
        final Calendar c_start_pin = Calendar.getInstance();
        c_start_pin.setTime(start_pin);
        Date end_pin = dateFormatter2.parse(dateFormatter2.format(date_end));
        final Calendar c_end_pin = Calendar.getInstance();
        c_end_pin.setTime(end_pin);

        Date cur_date = dateFormatter2.parse(date_month_year.getText().toString());
        final Calendar c_cur_date = Calendar.getInstance();
        c_cur_date.setTime(cur_date);

        button_left.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        boolean test = c_cur_date.getTime().getTime() == c_start_pin.getTime().getTime();
//                        Log.d("CUR", c_cur_date.getTime().getTime() + "");
//                        Log.d("PIN", c_start_pin.getTime().getTime() + "");

                        c_cur_date.add(Calendar.DATE, -1);
                        date_month_year.setText(dateFormatter2.format(c_cur_date.getTime()));
                        day.setText(new SimpleDateFormat("EEEE").format(c_cur_date.getTime()));

                        if (c_cur_date.getTime().getTime() == c_start_pin.getTime().getTime()) {
                            button_left.setClickable(false);
                            button_left.setBackgroundResource(R.drawable.ripple_oval_off);
                            button_right.setClickable(true);
                            button_right.setBackgroundResource(R.drawable.ripple_oval);
                        } else {
                            button_right.setClickable(true);
                            button_right.setBackgroundResource(R.drawable.ripple_oval);
                            button_left.setBackgroundResource(R.drawable.ripple_oval);
                        }

                        intent.putExtra("date", c_cur_date.getTime());
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                                new Fragment_EventList()).commit();
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
                            button_left.setClickable(true);
                            button_left.setBackgroundResource(R.drawable.ripple_oval);
                            button_right.setClickable(false);
                            button_right.setBackgroundResource(R.drawable.ripple_oval_off);
                        } else {
                            button_left.setClickable(true);
                            button_right.setBackgroundResource(R.drawable.ripple_oval);
                            button_left.setBackgroundResource(R.drawable.ripple_oval);
                        }

                        intent.putExtra("date", c_cur_date.getTime());
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_timeline_activity,
                                new Fragment_EventList()).commit();
                    }
                }
        );



    }

}
