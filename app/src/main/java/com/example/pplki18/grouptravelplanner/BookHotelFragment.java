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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.utils.Hotel;
import com.example.pplki18.grouptravelplanner.utils.PaginationScrollListener;
import com.example.pplki18.grouptravelplanner.utils.Place;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Hotel;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Place;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookHotelFragment extends Fragment {
    private RecyclerView recyclerViewPlace;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private TextView textView;
    private RVAdapter_Hotel adapter;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private SessionManager sessionManager;

    private String region;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hotel_list, container, false);
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
//                loadMorePlaces();
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
//                sendRequest(newQuery + " " + type);
//                adapter.clear();
//                progressBar.setVisibility(View.VISIBLE);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                if (TextUtils.isEmpty(newQuery)){
//                    sendRequest(type);
//                    adapter.clear();
//                    progressBar.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        generateToken();

    }

    public void generateToken() {
        Log.d("GET TOKEN HOTEL", "HERE");
        String secretKey = "6c484049beacda6541bf40c90e62e8e5";
        String url = "https://api-sandbox.tiket.com/apiv1/payexpress"
                + "?method=getToken&secretkey=" + secretKey + "&output=json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            String token = json.getString("token");

                            Log.d("TOKEN HOTEL", token);


                            sendRequest(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR HOTEL", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    public void sendRequest(String token) {
        Log.d("REQUEST HOTEL", "HERE");
        String url = "https://api-sandbox.tiket.com/search/hotel?q=" + region + "&startdate=2018-11-01&" +
                "night=1&enddate=2018-11-02&room=1&adult=2&token=" + token;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        populatePlaceRecyclerView(getHotels(response));
                        Log.d("HOTELS", response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR ALL HOTELS", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private List<Hotel> getHotels(String response) {
        ArrayList<Hotel> hotels = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray results = obj.optJSONArray("results");

            for (int i = 0 ; i < results.length() ; i++)
            {
                JSONObject hotelObj = new JSONObject(results.get(i).toString());

                Hotel hotel = new Hotel();
                hotel.setHotel_id(hotelObj.optString("id"));
                hotel.setName(hotelObj.optString("name"));
                hotel.setRating(hotelObj.optString("star_rating"));
                hotel.setRegion(hotelObj.optString("regional", "-"));

                hotels.add(hotel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("LIST OF PLACES", hotels.toString());
        return hotels;
    }

    private void populatePlaceRecyclerView(final List<Hotel> hotels) {
        Log.d("POPULATE LIST", "Displaying list of places.");

        RVAdapter_Hotel.ClickListener clickListener = new RVAdapter_Hotel.ClickListener() {
            @Override public void cardViewOnClick(View v, int position) {
//                Log.d("SELECTED PLACE ID", String.valueOf(adapter.getAll().get(position).getPlace_id()));
//
//                Intent intent = new Intent(getActivity(), PlaceActivity.class);
//                intent.putExtra("PLACE_ID", String.valueOf(adapter.getAll().get(position).getPlace_id()));
//                intent.putExtra("plan_id", plan_id);
//                intent.putExtra("date", event_date);
//                intent.putExtra("type", type);
//                startActivity(intent);
            }

            @Override public void addImageOnClick(View v, int position) {
//                setTime(adapter.getAll().get(position));
            }
        };

        progressBar.setVisibility(View.GONE);
        adapter.setListener(clickListener);
        adapter.addAll(hotels);

        recyclerViewPlace.setAdapter(adapter);

        if (!isLastPage) adapter.addLoadingFooter();
    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        recyclerViewPlace = (RecyclerView) getView().findViewById(R.id.rv);
        progressBar = (ProgressBar) getView().findViewById(R.id.main_progress);
        searchView = (SearchView) getView().findViewById(R.id.search_hotel);
        textView = (TextView) getView().findViewById(R.id.connection);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        adapter = new RVAdapter_Hotel(getContext());
        region = sessionManager.getCurrentRegion();
    }
}
