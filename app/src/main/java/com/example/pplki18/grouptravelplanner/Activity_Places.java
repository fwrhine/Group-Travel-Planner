package com.example.pplki18.grouptravelplanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.utils.Group;
import com.example.pplki18.grouptravelplanner.utils.Place;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Group;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Place;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_User;
import com.example.pplki18.grouptravelplanner.utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Places extends AppCompatActivity {

    private static final String TAG = "Activity_Places";

    private RecyclerView recyclerViewPlace;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__places);
        init();

        recyclerViewPlace.setHasFixedSize(true);
        recyclerViewPlace.setLayoutManager(linearLayoutManager);

        sendRequest();
    }

    public void sendRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurants+in+Tokyo&key=" + getString(R.string.api_key);


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        populatePlaceRecyclerView(getPlaces(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "REQUEST ERROR");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public List<Place> getPlaces(String result) {
        ArrayList<Place> places = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray results = obj.getJSONArray("results");

            for (int i = 0 ; i < results.length() ; i++)
            {
                JSONObject placeObj = new JSONObject(results.get(i).toString());

                Place place = new Place();
                place.setPlace_id(placeObj.getString("place_id"));
                place.setName(placeObj.getString("name"));
                place.setAddress(placeObj.getString("formatted_address"));
                place.setRating(placeObj.getInt("rating"));

                places.add(place);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return places;
    }

    private void populatePlaceRecyclerView(final List<Place> places) {
        Log.d(TAG, "populatePlaceRecyclerView: Displaying list of places in the ListView.");

        RVAdapter_Place adapter = new RVAdapter_Place(places, new RVAdapter_Place.ClickListener() {
            @Override public void onClick(View v, int position) {
                Log.d("ID", String.valueOf(places.get(position).getPlace_id()));
            }
        });

        recyclerViewPlace.setAdapter(adapter);
    }

    private void init() {
        recyclerViewPlace = (RecyclerView) findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
    }
}
