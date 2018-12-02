package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
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
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import com.example.pplki18.grouptravelplanner.data.Event;
import com.example.pplki18.grouptravelplanner.data.Hotel;
import com.example.pplki18.grouptravelplanner.data.Place;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Amenities;
import com.example.pplki18.grouptravelplanner.utils.VolleyResponseListener;
import com.example.pplki18.grouptravelplanner.utils.VolleySingleton;
import com.example.pplki18.grouptravelplanner.utils.VolleyUtils;

import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.data.Group;
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
import java.util.Objects;

import at.blogc.android.views.ExpandableTextView;

import static com.example.pplki18.grouptravelplanner.utils.HtmlParser.parseHotel;

@SuppressWarnings("SpellCheckingInspection")
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

    //hotel
    private TextView hotel_price;
    private ExpandableTextView hotel_desc;
    private Button button_toggle;
    private RecyclerView hotel_amenities1;
    private RecyclerView hotel_amenities2;
    private LinearLayoutManager linearLayoutManager1;
    private LinearLayoutManager linearLayoutManager2;
    private ConstraintLayout hotel_detail;
    private String checkInDate;
    private String checkOutDate;

    private ProgressBar progressBar;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventDuration;
    private TextView eventDescription;
    private RelativeLayout detailLayout;
    private ImageButton editEvent;
    private String plan_id;
    private String type;
    private List<String> eventIDs = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private Bundle bundle;
    private VolleyUtils volleyUtils;

    private String prevActivity;
    private String prevActivity2;
    private String prevFragment;

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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("");

        ic_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        if (prevFragment != null && (prevFragment.equals("HotelFragment"))) {
            sendRequestHotel();
            hotel_amenities1.setHasFixedSize(true);
            hotel_amenities1.setLayoutManager(linearLayoutManager1);
            hotel_amenities1.setItemAnimator(new DefaultItemAnimator());

            hotel_amenities2.setHasFixedSize(true);
            hotel_amenities2.setLayoutManager(linearLayoutManager2);
            hotel_amenities2.setItemAnimator(new DefaultItemAnimator());
        } else {
            sendRequestPlace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                    setResult(RESULT_OK, getIntent());
                }
            }
        }
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

    private void sendRequestPlace() {
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="
                + place_id + "&fields=name,formatted_address,rating,photo," +
                "opening_hours,website,international_phone_number,url&key="
                + getString(R.string.api_key);

        volleyUtils.getRequest(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "REQUEST PLACE DETAIL");
                populatePlaceView(getPlace(response));
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onError(VolleyError error) {
                Log.d(TAG, "PLACE DETAIL ERROR");
                noConnection(error);
            }
        });
    }

    private void sendRequestHotel() {
        volleyUtils.getRequest(place_id, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "REQUEST HOTEL DETAIL");
                populateHotelView(parseHotel(response));
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onError(VolleyError error) {
                Log.d(TAG, "HOTEL DETAIL ERROR");
                noConnection(error);
            }
        });
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
                builder.append(weekday_text.get(i)).append("\n");
            }
            // Convert to string.
            String result = builder.toString();

            open_hours.setText(result);
        }

//        google_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parseHotelList(place.getUrl()));
//                startActivity(browserIntent);
//            }
//        });

        getPlacePhoto(place.getPhoto());
    }

    private void populateHotelView(final Hotel hotel) {
        title.setText(hotel.getName());
        rating_num.setText(hotel.getRating());
        if (hotel.getRating().length() > 0) {
            rating.setRating(Float.valueOf(hotel.getRating()));
        }
        address.setText(hotel.getAddress());

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + hotel.getName() + ", "
                        + hotel.getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        phone.setText(hotel.getPhone_number());

        hotel_price.setText(hotel.getPrice());
        hotel_desc.setText(hotel.getDescription());

        // set interpolators for both expanding and collapsing animations
        hotel_desc.setInterpolator(new OvershootInterpolator());

        if (hotel.getDescription() != null) {
            button_toggle.setText(R.string.more);
            button_toggle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_arrow_down_grey, 0);
        }

        // toggle the ExpandableTextView
        button_toggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (hotel_desc.isExpanded())
                {
                    hotel_desc.collapse();
                    button_toggle.setText(R.string.more);
                    button_toggle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_arrow_down_grey, 0);
                }
                else
                {
                    hotel_desc.expand();
                    button_toggle.setText(R.string.less);
                    button_toggle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_arrow_up, 0);
                }
            }
        });

        if (!hotel.getAmenities().isEmpty()) {
            RVAdapter_Amenities adapter = new RVAdapter_Amenities(hotel.getAmenities().get(0));
            hotel_amenities1.setAdapter(adapter);

            ArrayList<String> amenities2 = hotel.getAmenities().get(1);
            amenities2.remove(amenities2.size()-1);
            adapter = new RVAdapter_Amenities(amenities2);
            hotel_amenities2.setAdapter(adapter);
        }

        getHotelPhoto(hotel.getPhoto().replace("photo-s", "photo-o"));
    }

    private void getPlacePhoto(String photo_reference) {
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
                Log.e(TAG, "Image Load Error");
            }
        });

        // Access the RequestQueue through singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(imageRequest);
    }

    private void getHotelPhoto(String photo_url) {
        // Request an image response from the provided URL.
        ImageRequest imageRequest = new ImageRequest(photo_url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        image.setImageBitmap(response);
                    }
                },  0, 0,  ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Image Load Error");
                    }
                });

        // Access the RequestQueue through singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(imageRequest);
    }

    private void setTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.set_time_dialog, null);
        final TextView start = dialogLayout.findViewById(R.id.start_text);
        TextView end = dialogLayout.findViewById(R.id.end_text);

        start.setText("Check-in Time");
        end.setText("Check-out Time");

        final TimePicker startTime = dialogLayout.findViewById(R.id.start_time);
        final TimePicker endTime = dialogLayout.findViewById(R.id.end_time);

        builder.setTitle("Set time");
        builder.setView(dialogLayout);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        toastMessage("Start " + startTime.getCurrentHour() + ":" + startTime.getCurrentMinute()
                                + " End " + endTime.getCurrentHour() + ":" + endTime.getCurrentMinute());

                        String start_time = startTime.getCurrentHour() + ":" + startTime.getCurrentMinute();
                        String end_time = endTime.getCurrentHour() + ":" + endTime.getCurrentMinute();
                        String prevActivity = getIntent().getStringExtra("ACTIVITY");
                        String prevFrag = getIntent().getStringExtra("prev_fragment");

                        //Log.d("PREVVV", prevActivity);
                        if (prevActivity != null) {
                            Log.d("NOT-EDIT", "ONE");
                            if (prevActivity.equals("CreateNewPlanActivity")) {
                                Log.d("NOT-EDIT", "TWO");
                                List<Event> events = getIntent().getParcelableArrayListExtra("events");
                                // TODO SALAH KAPRAH
                                if (prevFragment != null && prevFragment.equals("HotelFragment")) {
                                    Log.d("HotelFragment", checkInDate + checkOutDate);
                                    Event anEvent = saveEventLocally(checkInDate, start_time, "");
                                    events.add(anEvent);
                                    anEvent = saveEventLocally(checkOutDate, end_time, "");
                                    events.add(anEvent);
                                } else {
                                    Log.d("ElseFragment", checkInDate + checkOutDate);
                                    Event anEvent = saveEventLocally("", start_time, end_time);
                                    events.add(anEvent);
                                }

                                Intent intent = new Intent(PlaceActivity.this, Fragment_PlaceList.class);
                                intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                saveEventToPlan(start_time, end_time);

                                Intent intent = new Intent(PlaceActivity.this, Fragment_PlaceList.class);
                                intent.putExtra("ACTIVITY", "EditPlanActivity");
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } else if (prevFrag != null) {
                            Log.d("NOT-EDIT", "FOUR");
                            if (prevFrag.equals("Fragment_SuggestionList")) {
                                Log.d("CHECK-EDIT", (prevFrag != null)+"");
                                saveEventToSuggestion(start_time, end_time);
                                Intent intent = new Intent(PlaceActivity.this, Fragment_PlaceList.class);
                                intent.putExtra("ACTIVITY", "EditPlanActivity");
                                setResult(RESULT_OK, intent);
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

    private Event saveEventLocally(String date, String start_time, String end_time) {
        Event anEvent = new Event();

        if (prevFragment != null && prevFragment.equals("HotelFragment")) {
            Log.d("saveEventLocally", "HotelFragment");
            anEvent.setDate(date);
            anEvent.setTime_start(start_time);
        } else {
            Log.d("saveEventLocally", "ElseFragment");
            anEvent.setDate(getIntent().getStringExtra("date"));
            anEvent.setTime_start(start_time);
            anEvent.setTime_end(end_time);
        }
        anEvent.setQuery_id(place_id);
        anEvent.setTitle(title.getText().toString());
        anEvent.setLocation(address.getText().toString());
        anEvent.setWebsite(website.getText().toString());
        anEvent.setPhone(phone.getText().toString());
        anEvent.setRating(rating_num.getText().toString());
        anEvent.setType(getIntent().getStringExtra("type"));

        return anEvent;
    }

    private void saveEventToPlan(String start_time, final String end_time) {
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

        Event anEvent = new Event();
        anEvent.setQuery_id(place_id);
        anEvent.setTitle(title.getText().toString());
        anEvent.setLocation(address.getText().toString());
        anEvent.setWebsite(website.getText().toString());
        anEvent.setPhone(phone.getText().toString());
        anEvent.setRating(rating_num.getText().toString());
        anEvent.setType(type);

        if (type.equals("hotel")) {
            anEvent.setDate(checkInDate);
            anEvent.setTime_start(start_time);
        } else {
            anEvent.setDate(date);
            anEvent.setTime_start(start_time);
            anEvent.setTime_end(end_time);
        }

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
                        if (type.equals("hotel")) {
                            saveEventToPlan2(end_time);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void saveEventToPlan2(String time) {
        Event anEvent = new Event();
        anEvent.setQuery_id(place_id);
        anEvent.setTitle(title.getText().toString());
        anEvent.setLocation(address.getText().toString());
        anEvent.setWebsite(website.getText().toString());
        anEvent.setPhone(phone.getText().toString());
        anEvent.setRating(rating_num.getText().toString());
        anEvent.setType(type);

        anEvent.setDate(checkOutDate);
        anEvent.setTime_start(time);

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
        switch (type) {
            case "restaurants":
                ic_add.setImageResource(R.drawable.ic_restaurant_black);
                break;
            case "attractions":
                ic_add.setImageResource(R.drawable.ic_sunny_black);
                break;
            default:
                ic_add.setImageResource(R.drawable.ic_event_note_black);
                break;
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

    private void setEditEventButton() {
        editEvent.setColorFilter(R.color.colorRipple);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SimpleDateFormat dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

//                int event_id = getIntent().getIntExtra("event_id", -1);

                Bundle bundle = getIntent().getExtras();
                Objects.requireNonNull(bundle).putString("address", address.getText().toString());
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
        //queue = Volley.newRequestQueue(this);
        databaseHelper = new DatabaseHelper(this);

        //hotel
        hotel_price = findViewById(R.id.hotel_price);
        hotel_desc = findViewById(R.id.hotel_desc);
        button_toggle = findViewById(R.id.button_toggle);
        hotel_amenities1 = findViewById(R.id.list1);
        hotel_amenities2 = findViewById(R.id.list2);
        linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager2 = new LinearLayoutManager(this);
        hotel_detail = findViewById(R.id.hotel_detail);
        checkInDate = getIntent().getStringExtra("checkInDate");
        checkOutDate = getIntent().getStringExtra("checkOutDate");


        progressBar = findViewById(R.id.main_progress);
        volleyUtils = new VolleyUtils(this);
        prevActivity = getIntent().getStringExtra("ACTIVITY");
        prevActivity2 = getIntent().getStringExtra("PREV_ACTIVITY");
        prevFragment = getIntent().getStringExtra("FRAGMENT");


        eventDate = findViewById(R.id.event_detail_date);
        eventTime = findViewById(R.id.event_detail_time);
        eventDuration = findViewById(R.id.event_detail_duration);
        eventDescription = findViewById(R.id.event_detail_desc);
        detailLayout = findViewById(R.id.detail_layout);
        editEvent = findViewById(R.id.edit_event);
        plan_id = getIntent().getStringExtra("plan_id");
        type = getIntent().getStringExtra("type");

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

        if (prevFragment != null && (prevFragment.equals("HotelFragment"))) {
            open_hours.setVisibility(View.GONE);
            open_now.setVisibility(View.GONE);
            website.setVisibility(View.GONE);
        } else {
            hotel_price.setVisibility(View.GONE);
            hotel_detail.setVisibility(View.GONE);
        }

    }
}
