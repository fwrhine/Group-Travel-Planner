package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.example.pplki18.grouptravelplanner.data.Event;
import com.example.pplki18.grouptravelplanner.old_stuff.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.utils.Suggestion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Fragment_CustomEvent extends Fragment {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference planRef;
    DatabaseReference eventRef;
    StorageReference storageReference;

    private Button add_button;
    private DatabaseHelper databaseHelper;
    private EditText event_title;
    private EditText event_notes;
    private TimePicker start_timepicker;
    private TimePicker end_timepicker;
    private String plan_id;
    private String event_date;

    private String prevActivity;
    private List<String> eventIDs = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_event, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        init();

        if (prevActivity.equals("Fragment_SuggestionList")) {
            eventRef = firebaseDatabase.getReference().child("suggestions");
        }
        else {
            eventRef = firebaseDatabase.getReference().child("events");
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start_time = start_timepicker.getCurrentHour() + ":" + start_timepicker.getCurrentMinute();
                String end_time = end_timepicker.getCurrentHour() + ":" + end_timepicker.getCurrentMinute();
                String title = event_title.getText().toString();
                String description = event_notes.getText().toString();

                Event anEvent = new Event(title, event_date, start_time, end_time, "custom");
                anEvent.setDescription(description);

                Suggestion aSuggestion = new Suggestion(title, start_time, end_time,"custom");

                if (prevActivity.equals("CreateNewPlanActivity")) {
                    saveEventLocally(anEvent);
                }
                else if (prevActivity.equals("Fragment_SuggestionList")) {
                    saveEventToSuggestion(aSuggestion);
                }
                else {
                    saveEventToPlan(anEvent);
                }
                getActivity().finish();
            }
        });
    }

    private void saveEventLocally(Event anEvent) {
        events.add(anEvent);
        Intent intent = new Intent(getActivity(), CreateNewPlanActivity.class);
        intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

        getActivity().setResult(RESULT_OK, intent);
        getActivity().finish();
    }

    private void saveEventToPlan(Event anEvent) {
//        Log.d("SAVEVENT", "MASUK");
//        SQLiteDatabase db = databaseHelper.getWritableDatabase();
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
//        contentValues.put(EventContract.EventEntry.COL_TITLE, event_title.getText().toString());
//        contentValues.put(EventContract.EventEntry.COL_DESCRIPTION, event_notes.getText().toString());
//        contentValues.put(EventContract.EventEntry.COL_DATE, event_date);
//        Log.d("event date", event_date);
//        Log.d("plan_id", plan_id+"");
//        contentValues.put(EventContract.EventEntry.COL_TIME_START, start_time);
//        contentValues.put(EventContract.EventEntry.COL_TIME_END, end_time);
//        contentValues.put(EventContract.EventEntry.COL_TYPE, "custom");
//        long event_id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);
        final String eventId = eventRef.push().getKey();
        anEvent.setEvent_id(eventId);
        anEvent.setPlan_id(plan_id);
        anEvent.setCreator_id(firebaseUser.getUid());
        eventRef.child(eventId).setValue(anEvent);

        planRef = firebaseDatabase.getReference().child("plans").child(plan_id).child("events");
        getAllEventIDs(new EventIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                eventIDs = list;
                eventIDs.add(eventId);

                planRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        planRef.setValue(eventIDs);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void saveEventToSuggestion(Suggestion aSuggestion) {
        String groupId = getActivity().getIntent().getStringExtra("group_id");

        String planSuggestId = getArguments().getString("suggest_to_plan_id");
        String planSuggestName = getArguments().getString("suggest_to_plan_name");
        String planSuggestDate = getArguments().getString("suggest_to_plan_date");

        String suggestDesc = "For Plan '"+ planSuggestName +"' on "+ planSuggestDate;

        final String eventId = eventRef.push().getKey();
        aSuggestion.setSuggestion_id(eventId);
        aSuggestion.setDescription(suggestDesc);
        aSuggestion.setGroup_id(groupId);
        aSuggestion.setCreator_id(firebaseUser.getUid());
        aSuggestion.setPlan_id(planSuggestId);
        aSuggestion.setPlan_name(planSuggestName);
        aSuggestion.setPlan_date(planSuggestDate);

        eventRef.child(eventId).setValue(aSuggestion);

        planRef = firebaseDatabase.getReference().child("groups").child(groupId).child("suggestion");
        getAllEventIDs(new EventIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                eventIDs = list;
                eventIDs.add(eventId);

                planRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        planRef.setValue(eventIDs);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void getAllEventIDs(final EventIdCallback userIdCallback){
        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String eventId = postSnapshot.getValue(String.class); // String of groupID
                    eventIDs.add(eventId);
                }
                userIdCallback.onCallback(eventIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface EventIdCallback{
        void onCallback(List<String> list);
    }

    private void init() {
        add_button = (Button) getView().findViewById(R.id.add_button);
        databaseHelper = new DatabaseHelper(getActivity());
        event_title = (EditText) getView().findViewById(R.id.event_title);
        event_notes = (EditText) getView().findViewById(R.id.event_notes);
        start_timepicker = (TimePicker) getView().findViewById(R.id.start_time);
        end_timepicker = (TimePicker) getView().findViewById(R.id.end_time);
        plan_id = getArguments().getString("plan_id");
        event_date = getArguments().getString("date");

        prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");

        if (prevActivity == null) {
            prevActivity = getActivity().getIntent().getStringExtra("prev_fragment");
        }

        Log.d("Prev Frag", prevActivity);

        if (prevActivity.equals("Fragment_SuggestionList") | prevActivity.equals("CreateNewPlanActivity")) {
            events = getActivity().getIntent().getParcelableArrayListExtra("events");
        }
    }
}
