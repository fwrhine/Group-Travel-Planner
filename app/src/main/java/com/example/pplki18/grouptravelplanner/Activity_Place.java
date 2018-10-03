package com.example.pplki18.grouptravelplanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.utils.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Place extends AppCompatActivity {
    private static final String TAG = "RestaurantList";

    String place_id;
    Toolbar toolbar;
    TextView title;
    TextView rating;
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        init();

        sendRequest();

    }

    public void sendRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="
                + place_id + "&fields=name,formatted_address,rating,formatted_phone_number,photo,opening_hours,website&key="
                + getString(R.string.api_key);


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        populatePlaceView(getPlace(response));
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

    public Place getPlace(String response) {
        Place place = new Place();
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject results = obj.getJSONObject("result");

            place.setName(results.getString("name"));
            place.setRating(results.getInt("rating"));
            place.setAddress(results.getString("formatted_address"));
//            place.setPhone_number(results.getString("formatted_phone_number"));
//            place.setWebsite(results.getString("website"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }

//    public List<Place> getPlaces(String result) {
//        ArrayList<Place> places = new ArrayList<>();
//        try {
//            JSONObject obj = new JSONObject(result);
//            JSONArray results = obj.getJSONArray("results");
//
//            for (int i = 0 ; i < results.length() ; i++)
//            {
//                JSONObject placeObj = new JSONObject(results.get(i).toString());
//
//                Place place = new Place();
//                place.setPlace_id(placeObj.getString("place_id"));
//                place.setName(placeObj.getString("name"));
//                place.setAddress(placeObj.getString("formatted_address"));
//                place.setRating(placeObj.getInt("rating"));
//
//                places.add(place);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return places;
//    }

    public void populatePlaceView(Place place) {
        title.setText(place.getName());
        rating.setText(place.getRating() + "/5");
        address.setText(place.getAddress());

    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        place_id = getIntent().getStringExtra("PLACE_ID");
        title = (TextView) findViewById(R.id.title);
        rating = (TextView) findViewById(R.id.rating);
        address = (TextView) findViewById(R.id.address);
    }
}
