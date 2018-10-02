package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;



public class BookPlaneActivity extends Activity {

    EditText origin, destination, departureDate, returnDate;
    Button searchButton;

    TextView responseText;

    String token;
    RequestQueue queue;
    //ListView listTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_plane);

        origin = (EditText) findViewById(R.id.origin);
        destination = (EditText) findViewById(R.id.destination);
        departureDate = (EditText) findViewById(R.id.departureDate);
        returnDate = (EditText) findViewById(R.id.returnDate);

        searchButton = (Button) findViewById(R.id.searchButton);

        responseText = (TextView) findViewById(R.id.response);
        //listTravel = (ListView) findViewById(R.id.listTravel);

        token = "";
        queue = Volley.newRequestQueue(this);

        initSearch();
    }

    public void initSearch() {

        String secretKey = "6c484049beacda6541bf40c90e62e8e5";

        generateToken("https://api-sandbox.tiket.com/apiv1/payexpress?method=getToken&secretkey=" + secretKey + "&output=json");

        searchButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!validate()) {
                            return;
                        }

                        // --- API CALL --- //

                        // TODO CHANGE VAR NAME LATER M8
                        String start = origin.getText().toString();
                        String end = destination.getText().toString();
                        String depart = departureDate.getText().toString();
                        String arrival = returnDate.getText().toString();

                        String strUrl = "http://api-sandbox.tiket.com/search/flight?d="
                                + start + "&a=" + end + "&date=" + depart
                                + "&ret_date=" + arrival + "&token=" + token
                                + "&v=2&output=json";

                        getFlightData(strUrl);
                    }
                }
        );
    }

    public void setToken (String strToken) {
        token = strToken;
    }

    public void generateToken(String url) {
        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            String token = json.getString("token");
                            setToken(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "API call fail");
            }
        });

        queue.add(stringRequest);
    }

    public void getFlightData(String url) {
        // Request a string response from the provided URL.
        Log.d("URL", url);
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CHECK", response);
                        responseText.setText(response);

                        try {
                            JSONObject json = new JSONObject(response);
                            String depart = json.getString("departures");
                            Log.d("ARRAY", depart);

                            if (!json.isNull("departures")) {
                                JSONArray departure = json.getJSONArray("departures");
                                retrieveFlights(departure);
                            }

                            else {
                                responseText.setText("NO AVAILABLE FLIGHTS");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "Unknown Error!!!");
                responseText.setText("ERROR");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest2);
    }

    public void retrieveFlights (JSONArray departure) {
        // TODO retrieve flight data

    }

    public boolean validate() {
        boolean valid = true;

        String startLoc = origin.getText().toString();
        String endLoc = destination.getText().toString();
        String startDate = departureDate.getText().toString();
        String endDate = returnDate.getText().toString();

        if (startLoc.isEmpty() && startLoc.length() != 3) {
            Toast.makeText(BookPlaneActivity.this, "Please write the origin",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if (endLoc.isEmpty() && startLoc.length() != 3) {
            Toast.makeText(BookPlaneActivity.this, "Please write the destination",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if (startDate.isEmpty()) {
            Toast.makeText(BookPlaneActivity.this, "Please write the departure date",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if (endDate.isEmpty()) {
            Toast.makeText(BookPlaneActivity.this, "Please write the arrival date",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }
}
