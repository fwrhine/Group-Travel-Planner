package com.example.pplki18.grouptravelplanner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.utils.Flight;
import com.example.pplki18.grouptravelplanner.utils.FlightAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class BookPlaneFragment extends Fragment {

    private EditText origin, destination;
    private Button searchButton;

    private int plan_id;

    private String date;
    private String startDate;

    private String token;
    private HashMap<String, String> availableAirports;

    private RequestQueue queue;
    private int countUpdate;
    private ListView listTravel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_book_plane, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        origin = Objects.requireNonNull(getView()).findViewById(R.id.origin);
        destination = getView().findViewById(R.id.destination);

        searchButton = getView().findViewById(R.id.searchButton);
        listTravel = getView().findViewById(R.id.listTravel);

        plan_id = getArguments().getInt("plan_id");

        date = getArguments().getString("date");

        token = "";
        countUpdate = 0;
        queue = Volley.newRequestQueue(this.getActivity());

        availableAirports = new HashMap<>();

        initSearch();
    }

    private void initSearch() {

        String secretKey = "6c484049beacda6541bf40c90e62e8e5";
        String tokenUrl = "https://api-sandbox.tiket.com/apiv1/payexpress"
                + "?method=getToken&secretkey=" + secretKey + "&output=json";

        generateToken(tokenUrl, new TokenCallback() {
            @Override
            public void onCallback(String tokenStr) {
                token = tokenStr;

                fillAirportList(new AirportCallback() {
                    @Override
                    public void onCallback(HashMap<String, String> map) {
                        Log.d("MAP-FINAL", "map size: "+ map.size());
                        availableAirports = map;
                    }
                });
            }
        });

        try {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat date1 = new SimpleDateFormat("dd MMMM yyyy");
            Date date2 = date1.parse(date);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat date3 = new SimpleDateFormat("yyyy-MM-dd");
            startDate = date3.format(date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        searchButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (validate()) {

                            String startLoc = origin.getText().toString();
                            String endLoc = destination.getText().toString();
                            final String departDate = startDate;

                            if(availableAirports.containsKey(startLoc)) {

                                if(availableAirports.containsKey(endLoc)) {

                                    final String start = availableAirports.get(startLoc);
                                    final String end = availableAirports.get(endLoc);
                                    flightApiCall(token, start, end, departDate);
                                } else {
                                    Toast.makeText(BookPlaneFragment.this.getActivity()
                                            , "No Airports are in the arrival area"
                                            , Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(BookPlaneFragment.this.getActivity()
                                        , "No Airports are in the departure area"
                                        , Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        );
    }

    private void generateToken(String url, final TokenCallback callback) {
        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);

                            token = json.getString("token");
                            callback.onCallback(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private void fillAirportList(final AirportCallback callback) {
        String url = "https://api-sandbox.tiket.com/flight_api/all_airport?token=" + token + "&output=json";

        Log.d("FILL-MAP", "url -> " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("FILL-MAP", "start -> " + response);
                            JSONObject json = new JSONObject(response);

                            JSONObject allAirport = json.getJSONObject("all_airport");
                            JSONArray airportList = allAirport.getJSONArray("airport");

                            HashMap<String, String> airportMap = new HashMap<>();

                            for (int i = 0; i < airportList.length(); i++) {

                                JSONObject airportData = airportList.getJSONObject(i);

                                String airportLoc = airportData.getString("location_name");
                                String airportCode = airportData.getString("airport_code");


                                if (airportLoc.contains(" - ")) {
                                    String[] splitLoc = airportLoc.split(" - ");
                                    airportMap.put(splitLoc[1], airportCode);
                                }

                                else {
                                    airportMap.put(airportLoc, airportCode);
                                }
                            }
                            availableAirports = airportMap;

                            callback.onCallback(availableAirports);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private void flightApiCall (final String token, final String start, final String end,
                              final String depart) {
        // Request a string response from the provided URL.

        String url = "http://api-sandbox.tiket.com/search/flight?d="
                + start + "&a=" + end + "&date=" + depart + "&token=" + token + "&v=2&output=json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);

                            Log.d("RESPONSE", response);

                            // FOR TEST ONLY
                            String test = "test";
                            JSONObject testResponse = new JSONObject(test);

                            if (!testResponse.isNull("departures")) {
                                JSONObject results = testResponse.getJSONObject("departures");
                                JSONArray departure = results.getJSONArray("result");
                                getFlightData(start, end, departure);
                            }

                            else {
                                checkUpdate(token, start, end, depart);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("ERROR", error.toString());
            }
        });

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getFlightData (String startLoc, String endLoc, JSONArray departure)
            throws JSONException {
        Log.d("SUCCESS", "Correct response accepted");

        HashMap<String, ArrayList<String>> flightConnect = new HashMap<>();

        for (int i = 0; i < departure.length(); i++) {
            JSONObject flight = (JSONObject) departure.get(i);

            // Assume that only show flights without transit
            String id = flight.getString("flight_id");
            String name = flight.getString("airlines_name");
            String price = flight.getString("price_value");
            String flightNumber = flight.getString("flight_number");
            String departTime = flight.getString("simple_departure_time");
            String arriveTime = flight.getString("simple_arrival_time");

            ArrayList<String> tempList = new ArrayList<>();
            tempList.add(name);
            tempList.add(price);
            tempList.add(flightNumber);
            tempList.add(departTime);
            tempList.add(arriveTime);

            flightConnect.put(id, tempList);

        }
        Log.d("FLIGHT-LIST", "List size: " + flightConnect.size());
        setFlightList(startLoc, endLoc, flightConnect);
    }

    private void setFlightList (final String startLoc, final String endLoc,
                              HashMap<String, ArrayList<String>> map) {

        Log.d("FILL", "Start filling list");
        Log.d("plan_id", plan_id+"");

        ArrayList<String> keyArray = new ArrayList<>(map.keySet());

        ArrayList<Flight> availableFlights = new ArrayList<>();

        for (int i = 0; i < map.size(); i++) {
            String key = keyArray.get(i);

            ArrayList<String> moreFlightInfo = map.get(key);

            String airlineName = moreFlightInfo.get(0);
            String priceValue = "IDR " + moreFlightInfo.get(1);
            String flightNumber = moreFlightInfo.get(2);
            String departTime = moreFlightInfo.get(3);
            String arriveTime = moreFlightInfo.get(4);

            Flight flight = new Flight(airlineName, flightNumber, departTime, arriveTime,
                    priceValue, startLoc, endLoc);
            availableFlights.add(flight);
        }
        Log.d("ALMOST", "Almost finished");
        FlightAdapter adapter = new FlightAdapter(this.getActivity(), availableFlights);

        listTravel.setAdapter(adapter);
        listTravel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View viewItem, int i, long l) {

                final Dialog dialog = new Dialog(BookPlaneFragment.this.getActivity());

                dialog.setContentView(R.layout.popup_booking);

                dialog.show();

                Button noButton = dialog.findViewById(R.id.noButton);
                Button yesButton = dialog.findViewById(R.id.yesButton);

                noButton.setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        }
                );

                yesButton.setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                final TextView tvFlightNum = viewItem.findViewById(R.id.flightNumber);
                                final TextView tvDepartCity = viewItem.findViewById(R.id.departCity);
                                final TextView tvArriveCity = viewItem.findViewById(R.id.arriveCity);
                                final TextView tvDepartTime = viewItem.findViewById(R.id.departTime);
                                final TextView tvArriveTime = viewItem.findViewById(R.id.arriveTime);

                                DatabaseHelper myDb = new DatabaseHelper(BookPlaneFragment.this.getActivity());
                                SQLiteDatabase db = myDb.getReadableDatabase();

                                final String date = startDate;

                                ContentValues values = new ContentValues();

                                values.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
                                values.put(EventContract.EventEntry.COL_TITLE, "Travel Plan");
                                values.put(EventContract.EventEntry.COL_DESCRIPTION, "Flight for Transport");
                                values.put(EventContract.EventEntry.COL_DATE, date);
                                values.put(EventContract.EventEntry.COL_TYPE, "Transport");

                                values.put(EventContract.EventEntry.COL_ORIGIN, tvDepartCity.getText().toString());
                                values.put(EventContract.EventEntry.COL_DESTINATION, tvArriveCity.getText().toString());
                                values.put(EventContract.EventEntry.COL_DEPARTURE_TIME, tvDepartTime.getText().toString());
                                values.put(EventContract.EventEntry.COL_ARRIVAL_TIME, tvArriveTime.getText().toString());
                                values.put(EventContract.EventEntry.COL_TRANS_NUMBER, tvFlightNum.getText().toString());

                                long newRowId = db.insert(EventContract.EventEntry.TABLE_NAME, null, values);

                                if (newRowId != -1) {
                                    Toast.makeText(BookPlaneFragment.this.getActivity(), "Selected Flight : "
                                            + tvFlightNum.getText() , Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }

                                else {
                                    Toast.makeText(BookPlaneFragment.this.getActivity(),
                                            "Selection Failed!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        }
                );
            }
        });
        Log.d("DONE", "ListView populated");
    }

    private void checkUpdate (final String token, final String start, final String end,
                             final String depart) {
        String url = "https://api-sandbox.tiket.com/ajax/mCheckFlightUpdated?token=" + token
                + "&d=" + start + "&a=" + end + "&date=" + depart + "&time=134078435&output=json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("UPDATE", "update start");
                            JSONObject json = new JSONObject(response);
                            int update = Integer.parseInt(json.getString("update"));

                            if (countUpdate < 3 && update != 0) {
                                Log.d("UPDATE", "update complete");
                                countUpdate++;
                                flightApiCall(token, start, end, depart);
                            }
                            else {
                                Toast.makeText(BookPlaneFragment.this.getActivity(), "No flights are available",
                                        Toast.LENGTH_LONG).show();
                                countUpdate = 0;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private boolean validate() {
        boolean valid = true;

        String startLoc = origin.getText().toString();
        String endLoc = destination.getText().toString();

        if (startLoc.isEmpty()) {
            Toast.makeText(BookPlaneFragment.this.getActivity(), "Please write the origin",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if (endLoc.isEmpty()) {
            Toast.makeText(BookPlaneFragment.this.getActivity(), "Please write the destination",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

    private interface AirportCallback {
        void onCallback (HashMap<String, String> map);
    }

    private interface TokenCallback {
        void onCallback (String tokenStr);
    }
}