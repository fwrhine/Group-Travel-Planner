package com.example.pplki18.grouptravelplanner;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.utils.PaginationScrollListener;
import com.example.pplki18.grouptravelplanner.utils.Place;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Place;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_PlaceList extends Fragment {

    private RecyclerView recyclerViewPlace;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private TextView textView;
    private RVAdapter_Place adapter;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private String type;
    private String region;
    private String latitude;
    private String longitude;
    private String next_token;
    private RequestQueue queue;

    private int plan_id;
    private String event_date;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                startActivity(intent);
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
                        saveEventToPlan(place, start_time, end_time);
                        getActivity().finish();
                    }
                });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
            }
        });
        builder.show();
    }

    private void saveEventToPlan(Place place, String start_time, String end_time) {
        Log.d("SAVEVENT", "MASUK");
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
        contentValues.put(EventContract.EventEntry.COL_TITLE, place.getName());
        contentValues.put(EventContract.EventEntry.COL_LOCATION, place.getAddress());
        contentValues.put(EventContract.EventEntry.COL_DESCRIPTION, place.getWebsite());
        contentValues.put(EventContract.EventEntry.COL_DATE, event_date);
        Log.d("event date", event_date);
        Log.d("plan_id", plan_id+"");
        contentValues.put(EventContract.EventEntry.COL_TIME_START, start_time);
        contentValues.put(EventContract.EventEntry.COL_TIME_END, end_time);
        contentValues.put(EventContract.EventEntry.COL_PHONE, place.getPhone_number());
        contentValues.put(EventContract.EventEntry.COL_TYPE, type);
        contentValues.put(EventContract.EventEntry.COL_RATING, place.getRating());
        long event_id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);

    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        recyclerViewPlace = (RecyclerView) getView().findViewById(R.id.rv);
        progressBar = (ProgressBar) getView().findViewById(R.id.main_progress);
        searchView = (SearchView) getView().findViewById(R.id.search_place);
        textView = (TextView) getView().findViewById(R.id.connection);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        type = getArguments().getString("QUERY");
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        region = sessionManager.getCurrentRegion();
        latitude = getArguments().getString("LATITUDE");
        longitude = getArguments().getString("LONGITUDE");
        adapter = new RVAdapter_Place(getContext());
        plan_id = getArguments().getInt("plan_id");
        event_date = getArguments().getString("date");
        databaseHelper = new DatabaseHelper(getActivity());
        queue = Volley.newRequestQueue(getActivity());
    }
}
