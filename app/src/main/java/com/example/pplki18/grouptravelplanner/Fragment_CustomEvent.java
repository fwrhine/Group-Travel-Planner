package com.example.pplki18.grouptravelplanner;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.utils.Place;

public class Fragment_CustomEvent extends Fragment {

    private Button add_button;
    private DatabaseHelper databaseHelper;
    private EditText event_title;
    private EditText event_notes;
    private TimePicker start_timepicker;
    private TimePicker end_timepicker;
    private int plan_id;
    private String event_date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_event, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventToPlan();
                getActivity().finish();
            }
        });
    }

    private void saveEventToPlan() {
        Log.d("SAVEVENT", "MASUK");
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String start_time = start_timepicker.getCurrentHour() + ":" + start_timepicker.getCurrentMinute();
        String end_time = end_timepicker.getCurrentHour() + ":" + end_timepicker.getCurrentMinute();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
        contentValues.put(EventContract.EventEntry.COL_TITLE, event_title.getText().toString());
        contentValues.put(EventContract.EventEntry.COL_DESCRIPTION, event_notes.getText().toString());
        contentValues.put(EventContract.EventEntry.COL_DATE, event_date);
        Log.d("event date", event_date);
        Log.d("plan_id", plan_id+"");
        contentValues.put(EventContract.EventEntry.COL_TIME_START, start_time);
        contentValues.put(EventContract.EventEntry.COL_TIME_END, end_time);
        contentValues.put(EventContract.EventEntry.COL_TYPE, "custom");
        long event_id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);
    }

    private void init() {
        add_button = (Button) getView().findViewById(R.id.add_button);
        databaseHelper = new DatabaseHelper(getActivity());
        event_title = (EditText) getView().findViewById(R.id.event_title);
        event_notes = (EditText) getView().findViewById(R.id.event_notes);
        start_timepicker = (TimePicker) getView().findViewById(R.id.start_time);
        end_timepicker = (TimePicker) getView().findViewById(R.id.end_time);
        plan_id = getArguments().getInt("plan_id");
        event_date = getArguments().getString("date");
    }
}
