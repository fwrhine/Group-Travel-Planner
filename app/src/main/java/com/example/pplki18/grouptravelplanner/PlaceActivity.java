package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.data.Event;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.Place;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceActivity extends AppCompatActivity {
    private static final String TAG = "RestaurantList";
    private static final int REQUEST_CODE_EDIT_EVENT = 1;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference planRef;
    DatabaseReference eventRef;
    DatabaseReference suggestRef;
    StorageReference storageReference;

    private DatabaseHelper databaseHelper;

    private String place_id;
    private Toolbar toolbar;
    private TextView title;
    private TextView rating_num;
    private RatingBar rating;
    private TextView address;
    private TextView phone;
    private TextView website;
    private ImageView image;
    private TextView open_now;
    private TextView open_hours;
    private FloatingActionButton ic_add;
//    Button google_button;
    private ProgressBar progressBar;
    private RequestQueue queue;

    TextView eventDate;
    TextView eventTime;
    TextView eventDuration;
    TextView eventDescription;
    RelativeLayout detailLayout;
    ImageButton editEvent;

    private String plan_id;
    private List<String> eventIDs = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        eventRef = firebaseDatabase.getReference().child("events");
        suggestRef = firebaseDatabase.getReference().child("suggestions");
        storageReference = FirebaseStorage.getInstance().getReference();

        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        ic_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        sendRequest();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void noConnection(VolleyError volleyError) {
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "No internet connection.";
        } else if (volleyError instanceof NoConnectionError) {
            message = "No internet connection.";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection timeout.";
        }

        progressBar.setVisibility(View.GONE);
        toastMessage(message);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_EDIT_EVENT) {
            if (resultCode == Activity.RESULT_OK) {
                String start = data.getStringExtra("start_time");
                String end = data.getStringExtra("end_time");
                String description = data.getStringExtra("description");
                String date = data.getStringExtra("date");

                Event event = new Event(start, end);
                String duration = event.getTotal_time();

                String timeStr = start + " - " + end;

                eventDate.setText(date);
                eventTime.setText(timeStr);
                eventDuration.setText(duration);
                eventDescription.setText(description);

                String prevActivity = data.getStringExtra("PREV_ACTIVITY");
                if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
                    getIntent().putParcelableArrayListExtra("events", data.getParcelableArrayListExtra("events"));
                    getIntent().putExtra("ACTIVITY", prevActivity);
                    getIntent().putExtra("TEST", "ini test");

//                    Log.d("TRALALA2", prevActivity);
//                    ArrayList<Event> events = data.getParcelableArrayListExtra("events");
//                    for (Event e : events) {
//                        Log.d("DESC", e.getDescription());
//                    }
                    setResult(RESULT_OK, getIntent());
                }
            }
        }
    }

    private void sendRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="
                + place_id + "&fields=name,formatted_address,rating,photo," +
                "opening_hours,website,international_phone_number,url&key="
                + getString(R.string.api_key);


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        populatePlaceView(getPlace(response));
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "REQUEST ERROR");
                noConnection(error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private Place getPlace(String response) {
        Place place = new Place();
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject results = obj.getJSONObject("result");

            place.setName(results.optString("name"));
            place.setRating(results.optString("rating"));
            place.setAddress(results.optString("formatted_address"));
            place.setPhone_number(results.optString("international_phone_number"));
            place.setWebsite(results.optString("website"));
            place.setUrl(results.optString("url"));

            JSONObject opening_hours = results.optJSONObject("opening_hours");
            if (opening_hours != null) {
                place.setOpen_now(opening_hours.optBoolean("open_now"));
                JSONArray weekday_text = opening_hours.optJSONArray("weekday_text");
                if (weekday_text != null) {
                    ArrayList<String> listdata = new ArrayList<>();
                    for (int i=0;i<weekday_text.length();i++){
                        listdata.add(weekday_text.getString(i));
                    }
                    place.setWeekday_text(listdata);
                } else {
                    place.setWeekday_text(new ArrayList<String>());
                }
            }

            JSONArray photos = results.optJSONArray("photos");
            if (photos != null) {
                JSONObject first = new JSONObject(photos.get(0).toString());
                place.setPhoto(first.optString("photo_reference"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }

    private void populatePlaceView(final Place place) {
        title.setText(place.getName());
        rating_num.setText(place.getRating());
        rating.setRating(Float.valueOf(place.getRating()));
        address.setText(place.getAddress());

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(place.getUrl()));
                startActivity(browserIntent);
            }
        });

        phone.setText(place.getPhone_number());
        website.setText(place.getWebsite());

        if (place.getOpen_now() != null) {
            if (place.getOpen_now()) {
                open_now.setText(R.string.open_now);
            } else {
                open_now.setTextColor(getResources().getColor(R.color.red));
                open_now.setText(R.string.closed);
            }

            List<String> weekday_text = place.getWeekday_text();
            StringBuilder builder = new StringBuilder();

            // Loop and append values.
            for (int i = 0; i < weekday_text.size(); i++) {
                builder.append(weekday_text.get(i) + "\n");
            }
            // Convert to string.
            String result = builder.toString();

            open_hours.setText(result);
        }

//        google_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(place.getUrl()));
//                startActivity(browserIntent);
//            }
//        });

        Log.d("populate", place.getPhoto());
        getPhoto(place.getPhoto());
    }

    private void getPhoto(String photo_reference) {
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=900&photoreference="
                + photo_reference + "&key=" + getString(R.string.api_key);


        // Request an image response from the provided URL.
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        image.setImageBitmap(response);
                    }
                },  0, 0,  ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: ");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(imageRequest);
    }

    private void setTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.set_time_dialog, null);
        final TimePicker startTime = dialogLayout.findViewById(R.id.start_time);
        final TimePicker endTime = dialogLayout.findViewById(R.id.end_time);

        builder.setTitle("Set time");
        builder.setView(dialogLayout);

        final String prevFrag = getIntent().getStringExtra("prev_fragment");

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        toastMessage("Start " + startTime.getCurrentHour() + ":" + startTime.getCurrentMinute()
                                + " End " + endTime.getCurrentHour() + ":" + endTime.getCurrentMinute());

                        String start_time = startTime.getCurrentHour() + ":" + startTime.getCurrentMinute();
                        String end_time = endTime.getCurrentHour() + ":" + endTime.getCurrentMinute();
                        String prevActivity = getIntent().getStringExtra("ACTIVITY");
                        Log.d("PREVVV", prevActivity);
                        if (prevActivity != null) {
                            if (prevActivity.equals("CreateNewPlanActivity")) {
                                List<Event> events = getIntent().getParcelableArrayListExtra("events");
                                Event anEvent = saveEventLocally(start_time, end_time);
                                events.add(anEvent);
                                Log.d("HEHHE", "test");
                                Intent intent = new Intent(PlaceActivity.this, Fragment_PlaceList.class);
                                intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

                                setResult(RESULT_OK, intent);
                                finish();
                            }

                            else {

                                saveEventToPlan(start_time, end_time);
                                Intent intent = new Intent(PlaceActivity.this, Fragment_PlaceList.class);
                                intent.putExtra("ACTIVITY", "EditPlanActivity");
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } else if (prevFrag != null) {
                            if (prevFrag.equals("Fragment_SuggestionList")) {
                                saveEventToSuggestion(start_time, end_time);
                                finish();
                            }
                        }
                    }
                });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private Event saveEventLocally(String start_time, String end_time) {
        Event anEvent = new Event();
        anEvent.setQuery_id(place_id);
        anEvent.setTitle(title.getText().toString());
        anEvent.setLocation(address.getText().toString());
        anEvent.setWebsite(website.getText().toString());
        anEvent.setDate(getIntent().getStringExtra("date"));
        anEvent.setTime_start(start_time);
        anEvent.setTime_end(end_time);
        anEvent.setPhone(phone.getText().toString());
        anEvent.setType(getIntent().getStringExtra("type"));
        anEvent.setRating(rating_num.getText().toString());

        return anEvent;
    }

    private void saveEventToPlan(String start_time, String end_time) {
//        Log.d("SAVEVENT", "MASUK");
//        SQLiteDatabase db = databaseHelper.getWritableDatabase();
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(EventContract.EventEntry.COL_QUERY_ID, place_id);
//        contentValues.put(EventContract.EventEntry.COL_PLAN_ID, getIntent().getIntExtra("plan_id", -1));
//        contentValues.put(EventContract.EventEntry.COL_TITLE, title.getText().toString());
//        contentValues.put(EventContract.EventEntry.COL_LOCATION, address.getText().toString());
//        contentValues.put(EventContract.EventEntry.COL_WEBSITE, website.getText().toString());
//        contentValues.put(EventContract.EventEntry.COL_DATE, getIntent().getStringExtra("date"));
//        contentValues.put(EventContract.EventEntry.COL_TIME_START, start_time);
//        contentValues.put(EventContract.EventEntry.COL_TIME_END, end_time);
//        contentValues.put(EventContract.EventEntry.COL_PHONE, phone.getText().toString());
//        contentValues.put(EventContract.EventEntry.COL_TYPE, getIntent().getStringExtra("type"));
//        contentValues.put(EventContract.EventEntry.COL_RATING, rating_num.getText().toString());
//        long event_id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);
        String date = getIntent().getStringExtra("date");
        String type = getIntent().getStringExtra("type");

        Event anEvent = new Event(title.getText().toString(), date, start_time, end_time, type);
        anEvent.setQuery_id(place_id);
        anEvent.setLocation(address.getText().toString());
        anEvent.setWebsite(website.getText().toString());
        anEvent.setPhone(phone.getText().toString());
        anEvent.setRating(rating_num.getText().toString());

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

    private void saveEventToSuggestion(String start_time, String end_time) {
        String type = getIntent().getStringExtra("type");

        Suggestion aSuggestion = new Suggestion(title.getText().toString(), start_time, end_time, type);

        aSuggestion.setQuery_id(place_id);
        aSuggestion.setLocation(address.getText().toString());
        aSuggestion.setWebsite(website.getText().toString());
        aSuggestion.setPhone(phone.getText().toString());
        aSuggestion.setRating(rating_num.getText().toString());

        String groupId = getIntent().getStringExtra("group_id");

        String planSuggestId = getIntent().getStringExtra("suggest_to_plan_id");
        String planSuggestName = getIntent().getStringExtra("suggest_to_plan_name");
        String planSuggestDate = getIntent().getStringExtra("suggest_to_plan_date");

        String suggestDesc = "For Plan '"+ planSuggestName +"' on "+ planSuggestDate;

        final String eventId = suggestRef.push().getKey();
        aSuggestion.setSuggestion_id(eventId);
        aSuggestion.setDescription(suggestDesc);
        aSuggestion.setGroup_id(groupId);
        aSuggestion.setCreator_id(firebaseUser.getUid());
        aSuggestion.setPlan_id(planSuggestId);
        aSuggestion.setPlan_name(planSuggestName);
        aSuggestion.setPlan_date(planSuggestDate);

        suggestRef.child(eventId).setValue(aSuggestion);

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
                    String eventId = postSnapshot.getValue(String.class); // String of eventID
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

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setEventDetail() {
//        intent.putExtra("PLACE_ID", String.valueOf(anEvent.getQuery_id()));
        String date = getIntent().getStringExtra("date");
        String start = getIntent().getStringExtra("time_start");
        String end = getIntent().getStringExtra("time_end");
        String duration = getIntent().getStringExtra("duration");
        String desc = getIntent().getStringExtra("description");

        String timeStr = start + " - " + end;

        eventDate.setText(date);
        eventTime.setText(timeStr);
        eventDuration.setText(duration);

        if (desc.equals("")) {
            eventDescription.setText("edit to add description");
            eventDescription.setTypeface(eventDescription.getTypeface(), Typeface.ITALIC);
        } else {
            eventDescription.setText(desc);
        }

        String type = getIntent().getStringExtra("type");
        ic_add.setEnabled(false);
        if (type.equals("restaurants")) {
            ic_add.setImageResource(R.drawable.ic_restaurant_black);
        } else if (type.equals("attractions")) {
            ic_add.setImageResource(R.drawable.ic_sunny_black);
        } else {
            ic_add.setImageResource(R.drawable.ic_event_note_black);
        }
        if (bundle != null) {
            Group group = bundle.getParcelable("group");
            if (group != null && !group.getCreator_id().equals(firebaseUser.getUid())) {
                editEvent.setVisibility(View.GONE);
            } else {
                setEditEventButton();
            }
        } else {
            setEditEventButton();
        }
    }

    public void setEditEventButton() {
        editEvent.setColorFilter(R.color.colorRipple);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SimpleDateFormat dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

//                int event_id = getIntent().getIntExtra("event_id", -1);

                Bundle bundle = getIntent().getExtras();
                bundle.putString("address", address.getText().toString());
                bundle.putString("name", title.getText().toString());
                bundle.putString("description", eventDescription.getText().toString());
                bundle.putParcelableArrayList("events", (ArrayList<? extends Parcelable>) events);
                String prevActivity = getIntent().getStringExtra("ACTIVITY");
                if (prevActivity != null && prevActivity.equals("CreateNewPlanActivity")) {
                    bundle.putString("PREV_ACTIVITY", "CreateNewPlanActivity");
                }
//                bundle.putInt("event_id", event_id);

                Toast.makeText(PlaceActivity.this, "edit event", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PlaceActivity.this, EditEventActivity.class);
                intent.putExtras(bundle);

                startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT);
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        place_id = getIntent().getStringExtra("PLACE_ID");
        title = findViewById(R.id.title);
        rating_num = findViewById(R.id.rating_num);
        rating = findViewById(R.id.rating);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        website = findViewById(R.id.website);
        image = findViewById(R.id.image);
        open_now = findViewById(R.id.open_now);
        open_hours = findViewById(R.id.open_hours);
        ic_add = findViewById(R.id.ic_add);
//        google_button = (Button) findViewById(R.id.google_button);
        progressBar = findViewById(R.id.main_progress);
        queue = Volley.newRequestQueue(this);
        databaseHelper = new DatabaseHelper(this);

        String prevActivity = getIntent().getStringExtra("ACTIVITY");
        String prevActivity2 = getIntent().getStringExtra("PREV_ACTIVITY");
        plan_id = getIntent().getStringExtra("plan_id");

        eventDate = findViewById(R.id.event_detail_date);
        eventTime = findViewById(R.id.event_detail_time);
        eventDuration = findViewById(R.id.event_detail_duration);
        eventDescription = findViewById(R.id.event_detail_desc);
        detailLayout = findViewById(R.id.detail_layout);
        editEvent = findViewById(R.id.edit_event);

        bundle = getIntent().getBundleExtra("bundle");

        if (prevActivity != null && (prevActivity.equals("PlanActivity"))) {
            if (prevActivity2 != null && (prevActivity2.equals("CreateNewPlanActivity"))) {
                events = getIntent().getParcelableArrayListExtra("events");
                int index = getIntent().getIntExtra("index", -1);
                place_id = events.get(index).getQuery_id();
            }
            setEventDetail();

        } else {
            detailLayout.setVisibility(View.GONE);
        }

    }
}
