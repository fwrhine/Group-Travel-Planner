package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
    private RVAdapter_Place adapter;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    private String query;
    private String region;
    private String latitude;
    private String longitude;
    private String next_token;

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
            @Override public void onClick(View v, int position) {
                Log.d("SELECTED PLACE ID", String.valueOf(places.get(position).getPlace_id()));

                Intent intent = new Intent(getActivity(), Activity_Place.class);
                intent.putExtra("PLACE_ID", String.valueOf(places.get(position).getPlace_id()));
                startActivity(intent);
            }
        };

        progressBar.setVisibility(View.GONE);
        adapter.setListener(clickListener);
        adapter.addAll(places);

        recyclerViewPlace.setAdapter(adapter);

        if (!isLastPage) adapter.addLoadingFooter();
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
    }
}
