package com.example.pplki18.grouptravelplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.data.Event;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.Place;
import com.example.pplki18.grouptravelplanner.old_stuff.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.utils.PaginationScrollListener;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Place;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
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

import static android.app.Activity.RESULT_OK;

public class Fragment_PlaceList extends Fragment {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference planRef;
    DatabaseReference eventRef;
    DatabaseReference suggestionRef;
    StorageReference storageReference;

    private RecyclerView recyclerViewPlace;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private TextView textView;
    private RVAdapter_Place adapter;
    private ProgressBar progressBar;

    private String type;
    private String region;
    private String latitude;
    private String longitude;
    private String next_token;
    private RequestQueue queue;

    private String plan_id;
    private String event_date;
    private String prevActivity;

    private List<String> eventIDs = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private Group group;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_list, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "!");
        if (requestCode == 3) {
            Log.d("requestCode", "3!");
            if (resultCode == RESULT_OK) {
                Log.d("resultCode", "RESULT_OK!");
                String prevActivity = data.getStringExtra("ACTIVITY");
                if (prevActivity != null && prevActivity.equals("EditPlanActivity")) {
                    getActivity().finish();
                } else {
                    events = data.getParcelableArrayListExtra("events");

                    for (Event e : events) {
                        Log.d("testtt", e.getTitle());
                    }

                    Intent intent = new Intent(getActivity(), CreateNewPlanActivity.class);
                    intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                }

            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        eventRef = firebaseDatabase.getReference().child("events");
        suggestionRef = firebaseDatabase.getReference().child("suggestions");
        storageReference = FirebaseStorage.getInstance().getReference();

        init();

        textView.setVisibility(View.GONE);

        recyclerViewPlace.setHasFixedSize(true);
        recyclerViewPlace.setLayoutManager(linearLayoutManager);
        recyclerViewPlace.setItemAnimator(new DefaultItemAnimator());

        recyclerViewPlace.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                loadMorePlaces();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newQuery) {
                sendRequest(newQuery + " " + type);
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                if (TextUtils.isEmpty(newQuery)){
                    sendRequest(type);
                    adapter.clear();
                    progressBar.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        sendRequest(type);
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
        textView.setVisibility(View.VISIBLE);
        toastMessage(message);
    }

    private void sendRequest(String query) {
        String url ="https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query +
                "+in+" + region + "&fields=id,name,types,rating,formatted_address" +
                "&key=" + getString(R.string.api_key);

        Log.d("REQUEST", url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LIST RESPONSE", response);
                        populatePlaceRecyclerView(getPlaces(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LIST REQUEST ERROR", error.toString());
                noConnection(error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void loadMorePlaces() {
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?pagetoken="
                + next_token + "&key=" + getString(R.string.api_key);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("NEXT PAGE RESPONSE", response);
                        adapter.removeLoadingFooter();
                        isLoading = false;

                        adapter.addAll(getPlaces(response));

                        if (!isLastPage) adapter.addLoadingFooter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("NEXT PAGE REQUEST ERROR", error.toString());
                noConnection(error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private List<Place> getPlaces(String response) {
        ArrayList<Place> places = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray results = obj.optJSONArray("results");

            // check isLastPage
            next_token = obj.optString("next_page_token", null);
            if (next_token != null) {
                Log.d("NEXT TOKEN", next_token);
            } else {
                Log.d("NEXT TOKEN", "null");
                isLastPage = true;
            }

            for (int i = 0 ; i < results.length() ; i++)
            {
                JSONObject placeObj = new JSONObject(results.get(i).toString());

                Place place = new Place();
                place.setPlace_id(placeObj.optString("place_id"));
                place.setName(placeObj.optString("name"));
                place.setAddress(placeObj.optString("formatted_address"));
                place.setRating(placeObj.optString("rating", "-"));

                JSONArray photos = placeObj.optJSONArray("photos");
                if (photos != null) {
                    JSONObject first = new JSONObject(photos.get(0).toString());
                    place.setPhoto(first.optString("photo_reference"));
                }
                places.add(place);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("LIST OF PLACES", places.toString());
//        allPlaces.addAll(places);
        return places;
    }

    private void populatePlaceRecyclerView(final List<Place> places) {
        Log.d("POPULATE LIST", "Displaying list of places.");

        RVAdapter_Place.ClickListener clickListener = new RVAdapter_Place.ClickListener() {
            @Override public void cardViewOnClick(View v, int position) {
                Log.d("SELECTED PLACE ID", String.valueOf(adapter.getAll().get(position).getPlace_id()));

                Intent intent = new Intent(getActivity(), PlaceActivity.class);
                intent.putExtra("PLACE_ID", String.valueOf(adapter.getAll().get(position).getPlace_id()));
                intent.putExtra("plan_id", plan_id);
                intent.putExtra("date", event_date);
                intent.putExtra("type", type);
                intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);
                switch (prevActivity) {
                    case "CreateNewPlanActivity":
                        intent.putExtra("ACTIVITY", "CreateNewPlanActivity");
                        startActivityForResult(intent, 3);
                        break;
                    case "EditPlanActivity":
                        intent.putExtra("ACTIVITY", "EditPlanActivity");
                        startActivityForResult(intent, 3);
                        break;
                    default:
                        startActivity(intent);
                        break;
                }
            }

            @Override public void addImageOnClick(View v, int position) {
                setTime(adapter.getAll().get(position));

            }
        };

        progressBar.setVisibility(View.GONE);
        adapter.setListener(clickListener);
        adapter.addAll(places);

        recyclerViewPlace.setAdapter(adapter);

        if (!isLastPage) adapter.addLoadingFooter();
    }

    private void setTime(final Place place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.set_time_dialog, null);
        final TimePicker startTime = dialogLayout.findViewById(R.id.start_time);
        final TimePicker endTime = dialogLayout.findViewById(R.id.end_time);

        builder.setTitle("Set time");
        builder.setView(dialogLayout);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String start_time = startTime.getCurrentHour() + ":" + startTime.getCurrentMinute();
                        String end_time = endTime.getCurrentHour() + ":" + endTime.getCurrentMinute();
                        Log.d("prev_activity", prevActivity);
                        if (prevActivity.equals("CreateNewPlanActivity")) {
                            Event anEvent = saveEventLocally(place, start_time, end_time);
                            events.add(anEvent);

                            Intent intent = new Intent(getActivity(), CreateNewPlanActivity.class);
                            intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

                            getActivity().setResult(RESULT_OK, intent);
                            getActivity().finish();
                        } else if (prevActivity.equals("Fragment_SuggestionList")) {
                            saveEventToSuggestion(place, start_time, end_time);
                            getActivity().finish();
                        } else {
                            saveEventToPlan(place, start_time, end_time);
                            getActivity().finish();
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

    private Event saveEventLocally(Place place, String start_time, String end_time) {
        Event anEvent = new Event();
        anEvent.setQuery_id(place.getPlace_id());
        anEvent.setTitle(place.getName());
        anEvent.setLocation(place.getAddress());
        anEvent.setWebsite(place.getWebsite());
        anEvent.setDate(event_date);
        anEvent.setTime_start(start_time);
        anEvent.setTime_end(end_time);
        anEvent.setPhone(place.getPhone_number());
        anEvent.setType(type);
        anEvent.setRating(place.getRating());

        return anEvent;
    }

    private void saveEventToPlan(Place place, String start_time, String end_time) {
//        Log.d("SAVEVENT", "MASUK");
//        SQLiteDatabase db = databaseHelper.getWritableDatabase();
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(EventContract.EventEntry.COL_QUERY_ID, place.getPlace_id());
//        contentValues.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
//        contentValues.put(EventContract.EventEntry.COL_TITLE, place.getName());
//        contentValues.put(EventContract.EventEntry.COL_LOCATION, place.getAddress());
//        contentValues.put(EventContract.EventEntry.COL_WEBSITE, place.getWebsite());
//        contentValues.put(EventContract.EventEntry.COL_DATE, event_date);
//
//        contentValues.put(EventContract.EventEntry.COL_TIME_START, start_time);
//        contentValues.put(EventContract.EventEntry.COL_TIME_END, end_time);
//        contentValues.put(EventContract.EventEntry.COL_PHONE, place.getPhone_number());
//        contentValues.put(EventContract.EventEntry.COL_TYPE, type);
//        contentValues.put(EventContract.EventEntry.COL_RATING, place.getRating());
//        long event_id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);

        Event anEvent = new Event(place.getName(), event_date, start_time, end_time, type);

        anEvent.setQuery_id(place.getPlace_id());
        anEvent.setLocation(place.getAddress());
        anEvent.setWebsite(place.getWebsite());
        anEvent.setPhone(place.getPhone_number());
        anEvent.setRating(place.getRating());

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

    private void saveEventToSuggestion(Place place, String start_time, String end_time) {
        Suggestion aSuggestion = new Suggestion(place.getName(), start_time, end_time, type);

        aSuggestion.setQuery_id(place.getPlace_id());
        aSuggestion.setLocation(place.getAddress());
        aSuggestion.setWebsite(place.getWebsite());
        aSuggestion.setPhone(place.getPhone_number());
        aSuggestion.setRating(place.getRating());

        String groupId = getActivity().getIntent().getStringExtra("group_id");

        String planSuggestId = getActivity().getIntent().getStringExtra("suggest_to_plan_id");
        String planSuggestName = getActivity().getIntent().getStringExtra("suggest_to_plan_name");  //TODO: Its null?
        String planSuggestDate = getActivity().getIntent().getStringExtra("suggest_to_plan_date");  //TODO: Its null?

        String suggestDesc = "For Plan '"+ planSuggestName +"' on "+ planSuggestDate;

        final String eventId = suggestionRef.push().getKey();
        aSuggestion.setSuggestion_id(eventId);
        aSuggestion.setDescription(suggestDesc);
        aSuggestion.setGroup_id(groupId);
        aSuggestion.setCreator_id(firebaseUser.getUid());
        aSuggestion.setPlan_id(planSuggestId);
        aSuggestion.setPlan_name(planSuggestName);
        aSuggestion.setPlan_date(planSuggestDate);

        suggestionRef.child(eventId).setValue(aSuggestion);

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

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        recyclerViewPlace = getView().findViewById(R.id.rv);
        progressBar = getView().findViewById(R.id.main_progress);
        searchView = getView().findViewById(R.id.search_place);
        textView = getView().findViewById(R.id.connection);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        type = getArguments().getString("QUERY");
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        region = sessionManager.getCurrentRegion();
        latitude = getArguments().getString("LATITUDE");
        longitude = getArguments().getString("LONGITUDE");
        adapter = new RVAdapter_Place(getContext());
        plan_id = getArguments().getString("plan_id");
        event_date = getArguments().getString("date");
        //DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");
        queue = Volley.newRequestQueue(getActivity());

        if (prevActivity == null) {
            prevActivity = getActivity().getIntent().getStringExtra("prev_fragment");
        }

        Log.d("Prev Frag", prevActivity);

        if (prevActivity.equals("Fragment_SuggestionList") | prevActivity.equals("CreateNewPlanActivity")) {
            events = getActivity().getIntent().getParcelableArrayListExtra("events");
        }
    }
}
