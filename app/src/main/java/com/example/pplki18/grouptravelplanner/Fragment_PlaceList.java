package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.data.PlanContract;
import com.example.pplki18.grouptravelplanner.utils.Event;
import com.example.pplki18.grouptravelplanner.utils.PaginationScrollListener;
import com.example.pplki18.grouptravelplanner.utils.Place;
import com.example.pplki18.grouptravelplanner.utils.Plan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Place;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Fragment_PlaceList extends Fragment {

    private RecyclerView recyclerViewPlace;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private RVAdapter_Place adapter;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private String query;
    private String region;
    private String latitude;
    private String longitude;
    private String next_token;

    private int plan_id;
    private String event_date;
    private String prevActivity;
    private List<Event> events;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_list, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
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
        init();

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
                sendRequest(newQuery);
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                if (TextUtils.isEmpty(newQuery)){
                    sendRequest(query);
                    adapter.clear();
                    progressBar.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        sendRequest(query);
    }

    public void sendRequest(String query) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query + "+in+" + region
                + "&fields=id,name,types,rating,formatted_address&key=" + getString(R.string.api_key);

//        String url ="https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query
//                + "&locationbias=circle:5000@" + latitude + "," + longitude
//                + "&fields=id,name,types,rating,formatted_address&key=" + getString(R.string.api_key);

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
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void loadMorePlaces() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?pagetoken="
                + next_token + "&key=AIzaSyB4QT2f2fyMQ8gDILgUEi5xBl_NKiGt_fo";

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
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public List<Place> getPlaces(String response) {
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
        return places;
    }

    private void populatePlaceRecyclerView(final List<Place> places) {
        Log.d("POPULATE LIST", "Displaying list of places.");

        RVAdapter_Place.ClickListener clickListener = new RVAdapter_Place.ClickListener() {
            @Override public void cardViewOnClick(View v, int position) {
                Log.d("SELECTED PLACE ID", String.valueOf(places.get(position).getPlace_id()));

                Intent intent = new Intent(getActivity(), PlaceActivity.class);
                intent.putExtra("PLACE_ID", String.valueOf(places.get(position).getPlace_id()));
                intent.putExtra("plan_id", plan_id);
                intent.putExtra("date", event_date);
                intent.putExtra("type", query);
                intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);
                if (prevActivity.equals("CreateNewPlanActivity")) {
                    intent.putExtra("ACTIVITY", "CreateNewPlanActivity");
                    getActivity().startActivityForResult(intent, 3);
                } else if (prevActivity.equals("EditPlanActivity")){
                    intent.putExtra("ACTIVITY", "EditPlanActivity");
                    getActivity().startActivityForResult(intent, 3);
                } else {
                    startActivity(intent);
                }
            }

            @Override public void addImageOnClick(View v, int position) {
                setTime(places, position);
            }
        };

        progressBar.setVisibility(View.GONE);
        adapter.setListener(clickListener);
        adapter.addAll(places);

        recyclerViewPlace.setAdapter(adapter);

        if (!isLastPage) adapter.addLoadingFooter();
    }

    public void setTime(final List<Place> places, final int position) {
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
                        toastMessage("Start " + startTime.getCurrentHour() + ":" + startTime.getCurrentMinute()
                                + " End " + endTime.getCurrentHour() + ":" + endTime.getCurrentMinute());
                        String start_time = startTime.getCurrentHour() + ":" + startTime.getCurrentMinute();
                        String end_time = endTime.getCurrentHour() + ":" + endTime.getCurrentMinute();

                        Log.d("prev_activity", prevActivity);
                        if (prevActivity.equals("CreateNewPlanActivity")) {
                            Event anEvent = saveEventLocally(places, position, start_time, end_time);
                            events.add(anEvent);

                            Intent intent = new Intent(getActivity(), CreateNewPlanActivity.class);
                            intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);
                            intent.putExtra("ACTIVITY", "Fragment_PlaceList");
                            Log.d("prev activity", "createnewplan");
                            getActivity().setResult(RESULT_OK, intent);
                            getActivity().finish();
                        } else {
                            saveEventToPlan(places, position, start_time, end_time);
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

    private Event saveEventLocally(List<Place> places, int position, String start_time, String end_time) {
        Event anEvent = new Event();
        anEvent.setTitle(places.get(position).getName());
        anEvent.setLocation(places.get(position).getAddress());
        anEvent.setDescription(places.get(position).getWebsite());
        anEvent.setDate(event_date);
        anEvent.setTime_start(start_time);
        anEvent.setTime_end(end_time);
        anEvent.setPhone(places.get(position).getPhone_number());
        anEvent.setType(query);
        anEvent.setRating(places.get(position).getRating());

        return anEvent;
    }

    private void saveEventToPlan(List<Place> places, int position, String start_time, String end_time) {
        Log.d("SAVEVENT", "MASUK");
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
        contentValues.put(EventContract.EventEntry.COL_TITLE, places.get(position).getName());
        contentValues.put(EventContract.EventEntry.COL_LOCATION, places.get(position).getAddress());
        contentValues.put(EventContract.EventEntry.COL_DESCRIPTION, places.get(position).getWebsite());
        contentValues.put(EventContract.EventEntry.COL_DATE, event_date);
//        Log.d("event date", event_date);
//        Log.d("plan_id", plan_id+"");
        contentValues.put(EventContract.EventEntry.COL_TIME_START, start_time);
        contentValues.put(EventContract.EventEntry.COL_TIME_END, end_time);
        contentValues.put(EventContract.EventEntry.COL_PHONE, places.get(position).getPhone_number());
        contentValues.put(EventContract.EventEntry.COL_TYPE, query);
        contentValues.put(EventContract.EventEntry.COL_RATING, places.get(position).getRating());
        long event_id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);

    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        recyclerViewPlace = (RecyclerView) getView().findViewById(R.id.rv);
        progressBar = (ProgressBar) getView().findViewById(R.id.main_progress);
        searchView = (SearchView) getView().findViewById(R.id.search_place);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        query = getArguments().getString("QUERY");
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        region = sessionManager.getCurrentRegion();
        latitude = getArguments().getString("LATITUDE");
        longitude = getArguments().getString("LONGITUDE");
        adapter = new RVAdapter_Place(getContext());
        plan_id = getArguments().getInt("plan_id");
        event_date = getArguments().getString("date");
        databaseHelper = new DatabaseHelper(getActivity());

        prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");
        if (prevActivity.equals("CreateNewPlanActivity")) {
            events = getActivity().getIntent().getParcelableArrayListExtra("events");
        }
    }
}
