package com.example.pplki18.grouptravelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.data.PlanContract;
import com.example.pplki18.grouptravelplanner.utils.Event;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

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

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter1;
    private SimpleDateFormat dateFormatter2;
    private Date date_start;
    private Date date_end;
    private String plan_name;

    private List<Event> events;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                events = data.getParcelableArrayListExtra("events");
//                for(Event e : events) {
//                    Log.d("testtt", e.getTitle());
//                }
//                Log.d("RESULT_OK", "masuk sini");
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

//        String prevActivity = intent.getStringExtra("ACTIVITY");
////        if (prevActivity != null && prevActivity.equals("PlaceActivity")) {
////            events = intent.getParcelableArrayListExtra("events");
//////            for(Event e : events) {
//////                Log.d("testtt2", e.getTitle());
//////            }
////            beginFragmentEventList();
////        }

        databaseHelper = new DatabaseHelper(CreateNewPlanActivity.this);
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

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
                        ;
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
                        ;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void savePlanToDB(String plan_name_fix, String plan_desc) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String start_day = dateFormatter2.format(date_start);
        String end_day = dateFormatter2.format(date_end);
        int total_days = Integer.parseInt(trip_days.getText().toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlanContract.PlanEntry.COL_PLAN_NAME, plan_name_fix);
        contentValues.put(PlanContract.PlanEntry.COL_USER_ID, user.get(SessionManager.KEY_ID));
        contentValues.put(PlanContract.PlanEntry.COL_START_DAY, start_day);
        contentValues.put(PlanContract.PlanEntry.COL_END_DAY, end_day);
        contentValues.put(PlanContract.PlanEntry.COL_TOTAL_DAY, total_days);
        contentValues.put(PlanContract.PlanEntry.COL_DESCRIPTION, plan_desc);
        long plan_id = db.insert(PlanContract.PlanEntry.TABLE_NAME, null, contentValues);

        saveEventToDB(db, (int) plan_id);
    }

    private void saveEventToDB(SQLiteDatabase db, int plan_id) {
        for(Event e : events) {
//            Log.d("testtt", e.getTitle() + ", " + e.getDate());
            String type = e.getType();
            ContentValues contentValues = new ContentValues();
            if (type.equals("restaurants") || type.equals("attractions") || type.equals("custom")) {

                contentValues.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
                contentValues.put(EventContract.EventEntry.COL_QUERY_ID, e.getQuery_id());
                contentValues.put(EventContract.EventEntry.COL_TITLE, e.getTitle());
                contentValues.put(EventContract.EventEntry.COL_LOCATION, e.getLocation());
                contentValues.put(EventContract.EventEntry.COL_DESCRIPTION, e.getDescription());
                contentValues.put(EventContract.EventEntry.COL_DATE, e.getDate());
                contentValues.put(EventContract.EventEntry.COL_TIME_START, e.getTime_start());
                contentValues.put(EventContract.EventEntry.COL_TIME_END, e.getTime_end());
                contentValues.put(EventContract.EventEntry.COL_PHONE, e.getPhone());
                contentValues.put(EventContract.EventEntry.COL_TYPE, type);
                contentValues.put(EventContract.EventEntry.COL_RATING, e.getRating());
                contentValues.put(EventContract.EventEntry.COL_WEBSITE, e.getWebsite());
                long event_id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);

            } else if (type.equals("flights") || type.equals("trains")) {
                contentValues.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
                contentValues.put(EventContract.EventEntry.COL_TITLE, "Flight");
                contentValues.put(EventContract.EventEntry.COL_DESCRIPTION, "Flight for Transport");
                contentValues.put(EventContract.EventEntry.COL_DATE, e.getDate());
                contentValues.put(EventContract.EventEntry.COL_TYPE, "flights");

                contentValues.put(EventContract.EventEntry.COL_ORIGIN, e.getOrigin());
                contentValues.put(EventContract.EventEntry.COL_DESTINATION, e.getDestination());
                contentValues.put(EventContract.EventEntry.COL_DEPARTURE_TIME, e.getDeparture_time());
                contentValues.put(EventContract.EventEntry.COL_ARRIVAL_TIME, e.getArrival_time());
                contentValues.put(EventContract.EventEntry.COL_TRANS_NUMBER, e.getTransport_number());
                long event_id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);

            } else if (type.equals("hotels")) {

            }

        }
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
                            myIntent.putExtra("date_start", date_start);
                            myIntent.putExtra("date_end", date_end);

                            myIntent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

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

}
