package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.utils.Place;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Activity_Place extends AppCompatActivity {
    private static final String TAG = "RestaurantList";

    String place_id;
    Toolbar toolbar;
    TextView title;
    TextView rating;
    TextView address;
    TextView phone;
    TextView website;
    ImageView image;
    Button google_button;
//    ProgressBar progressBar;

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
                + place_id + "&fields=name,formatted_address,rating,formatted_phone_number,photo,opening_hours,website,url&key="
                + getString(R.string.api_key);


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        populatePlaceView(getPlace(response));
//                        progressBar.setVisibility(View.GONE);
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

            place.setName(results.optString("name"));
            place.setRating(results.optString("rating", "-"));
            place.setAddress(results.optString("formatted_address"));
            place.setPhone_number(results.optString("formatted_phone_number"));
            place.setWebsite(results.optString("website"));
            place.setUrl(results.optString("url"));

            JSONArray photos = results.getJSONArray("photos");

            JSONObject first = new JSONObject(photos.get(0).toString());

            place.setPhoto(first.optString("photo_reference"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }

    public void populatePlaceView(final Place place) {
        title.setText(place.getName());
        rating.setText(place.getRating() + "/5");
        address.setText(place.getAddress());
        phone.setText(place.getPhone_number());
        website.setText(place.getWebsite());
        google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(place.getUrl()));
                startActivity(browserIntent);
            }
        });

        Log.d("populate", place.getPhoto());
        getPhoto(place.getPhoto());
    }

    public void getPhoto(String photo_reference) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
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

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        place_id = getIntent().getStringExtra("PLACE_ID");
        title = (TextView) findViewById(R.id.title);
        rating = (TextView) findViewById(R.id.rating);
        address = (TextView) findViewById(R.id.address);
        phone = (TextView) findViewById(R.id.phone);
        website = (TextView) findViewById(R.id.website);
        image = (ImageView) findViewById(R.id.image);
        google_button = (Button) findViewById(R.id.google_button);
//        progressBar = (ProgressBar) findViewById(R.id.progress);
    }
}
