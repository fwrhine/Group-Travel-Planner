package com.example.pplki18.grouptravelplanner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.utils.Flight;
import com.example.pplki18.grouptravelplanner.utils.FlightAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BookPlaneActivity extends Activity {

    EditText origin, destination;
    EditText departureDay, departureMonth, departureYear;
    Button searchButton;

    String token;

    RequestQueue queue;
    int countUpdate;
    ListView listTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_plane);

        origin = (EditText) findViewById(R.id.origin);
        destination = (EditText) findViewById(R.id.destination);

        departureDay = (EditText) findViewById(R.id.departureDay);
        departureMonth = (EditText) findViewById(R.id.departureMonth);
        departureYear = (EditText) findViewById(R.id.departureYear);

        searchButton = (Button) findViewById(R.id.searchButton);
        listTravel = (ListView) findViewById(R.id.listTravel);

        token = "";
        countUpdate = 0;
        queue = Volley.newRequestQueue(this);

        initSearch();
    }

    public void initSearch() {

        searchButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!validate()) {
                            return;
                        }

                        // TODO CHANGE VAR NAME LATER M8
                        final String start = origin.getText().toString();
                        final String end = destination.getText().toString();
                        final String depart = departureYear.getText().toString() + "-" + departureMonth.getText().toString() + "-" + departureDay.getText().toString();

                        String secretKey = "6c484049beacda6541bf40c90e62e8e5";
                        String tokenUrl = "https://api-sandbox.tiket.com/apiv1/payexpress"
                                + "?method=getToken&secretkey=" + secretKey + "&output=json";
                        generateToken(tokenUrl, start, end, depart);
                    }
                }
        );
    }

    public void setToken (String strToken) {
        token = strToken;
    }

    public void generateToken(String url, final String start, final String end,
                              final String depart) {
        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            String token = json.getString("token");
                            getFlightData(token, start, end, depart);
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

    public void getFlightData(final String token, final String start, final String end,
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
                            Log.d("JSON", "there is response");

                            String test = "{\"output_type\":\"json\",\"round_trip\":true,\"search_queries\":{\"from\":\"BPN\",\"to\":\"MES\",\"date\":\"2013-02-05\",\"ret_date\":\"2013-02-10\",\"adult\":1,\"child\":0,\"infant\":0},\"go_det\":{\"dep_airport\":{\"airport_code\":\"BPN\",\"international\":\"1\",\"trans_name_id\":\"7565\",\"business_name\":\"SEPINGGAN\",\"business_name_trans_id\":\"5924\",\"business_id\":\"20350\",\"country_name\":\"Indonesia \",\"city_name\":\"Balikpapan\",\"province_name\":\"Kalimantan Timur\",\"location_name\":\"BalikPapan\"},\"arr_airport\":{\"airport_code\":\"MES\",\"international\":\"1\",\"trans_name_id\":\"7585\",\"business_name\":\"POLONIA\",\"business_name_trans_id\":\"5949\",\"business_id\":\"20375\",\"country_name\":\"Indonesia \",\"city_name\":\"Medan\",\"province_name\":\"Sumatera Utara\",\"location_name\":\"Medan\"},\"date\":\"2013-02-05\",\"formatted_date\":\"05 February 2013\"},\"ret_det\":{\"dep_airport\":{\"airport_code\":\"MES\",\"international\":\"1\",\"trans_name_id\":\"7585\",\"business_name\":\"POLONIA\",\"business_name_trans_id\":\"5949\",\"business_id\":\"20375\",\"country_name\":\"Indonesia \",\"city_name\":\"Medan\",\"province_name\":\"Sumatera Utara\",\"location_name\":\"Medan\"},\"arr_airport\":{\"airport_code\":\"BPN\",\"international\":\"1\",\"trans_name_id\":\"7565\",\"business_name\":\"SEPINGGAN\",\"business_name_trans_id\":\"5924\",\"business_id\":\"20350\",\"country_name\":\"Indonesia \",\"city_name\":\"Balikpapan\",\"province_name\":\"Kalimantan Timur\",\"location_name\":\"BalikPapan\"},\"date\":\"2013-02-10\",\"formatted_date\":\"10 February 2013\"},\"diagnostic\":{\"status\":200,\"elapsetime\":\"1.5670\",\"memoryusage\":\"20.37MB\",\"confirm\":\"success\",\"lang\":\"en\",\"currency\":\"IDR\"},\"departures\":{\"result\":[{\"flight_id\":\"3789714\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-763\\/JT-382\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"15:10\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (06:00 - 07:10), CGK - MES (12:50 - 15:10)\",\"duration\":\"10 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-763\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"07:10\"},{\"flight_number\":\"JT-382\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"12:50\",\"simple_arrival_time\":\"15:10\"}]}},{\"flight_id\":\"3789712\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-673\\/JT-382\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:45\",\"simple_arrival_time\":\"15:10\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (07:45 - 08:55), CGK - MES (12:50 - 15:10)\",\"duration\":\"8 h 25 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-673\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"07:45\",\"simple_arrival_time\":\"08:55\"},{\"flight_number\":\"JT-382\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"12:50\",\"simple_arrival_time\":\"15:10\"}]}},{\"flight_id\":\"3789711\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-763\\/JT-398\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"14:40\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (06:00 - 07:10), CGK - MES (12:20 - 14:40)\",\"duration\":\"9 h 40 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-763\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"07:10\"},{\"flight_number\":\"JT-398\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"12:20\",\"simple_arrival_time\":\"14:40\"}]}},{\"flight_id\":\"3789715\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-673\\/JT-384\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:45\",\"simple_arrival_time\":\"16:20\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (07:45 - 08:55), CGK - MES (14:00 - 16:20)\",\"duration\":\"9 h 35 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-673\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"07:45\",\"simple_arrival_time\":\"08:55\"},{\"flight_number\":\"JT-384\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"14:00\",\"simple_arrival_time\":\"16:20\"}]}},{\"flight_id\":\"4755478\",\"airlines_name\":\"SRIWIJAYA\",\"flight_number\":\"SJ-231\\/SJ-020\",\"price_value\":\"2690000.00\",\"timestamp\":\"2013-01-14 16:57:55\",\"price_adult\":\"2690000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"09:00\",\"simple_arrival_time\":\"15:40\",\"stop\":\"3 Stops\",\"long_via\":\"Yogyakarta (JOG) - Jakarta (CGK) - Padang (PDG)\",\"full_via\":\"BPN - CGK (09:00 - 11:20), CGK - MES (12:30 - 15:40)\",\"duration\":\"7 h 40 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_sriwijaya_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"SJ-231\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"09:00\",\"simple_arrival_time\":\"11:20\"},{\"flight_number\":\"SJ-020\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"12:30\",\"simple_arrival_time\":\"15:40\"}]}},{\"flight_id\":\"3789719\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-367\\/JT-973\",\"price_value\":\"1814000.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1814000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:50\",\"simple_arrival_time\":\"16:00\",\"stop\":\"2 Stops\",\"long_via\":\"Surabaya (SUB)\",\"full_via\":\"BPN - SUB (06:50 - 07:20), SUB - MES (11:50 - 16:00)\",\"duration\":\"10 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-367\",\"departure_city\":\"BPN\",\"arrival_city\":\"SUB\",\"simple_departure_time\":\"06:50\",\"simple_arrival_time\":\"07:20\"},{\"flight_number\":\"JT-973\",\"departure_city\":\"SUB\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"11:50\",\"simple_arrival_time\":\"16:00\"}]}},{\"flight_id\":\"3789718\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-361\\/JT-973\",\"price_value\":\"1550000.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1550000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"09:15\",\"simple_arrival_time\":\"16:00\",\"stop\":\"2 Stops\",\"long_via\":\"Surabaya (SUB)\",\"full_via\":\"BPN - SUB (09:15 - 09:45), SUB - MES (11:50 - 16:00)\",\"duration\":\"7 h 45 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-361\",\"departure_city\":\"BPN\",\"arrival_city\":\"SUB\",\"simple_departure_time\":\"09:15\",\"simple_arrival_time\":\"09:45\"},{\"flight_number\":\"JT-973\",\"departure_city\":\"SUB\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"11:50\",\"simple_arrival_time\":\"16:00\"}]}},{\"flight_id\":\"3789717\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-761\\/JT-384\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"09:45\",\"simple_arrival_time\":\"16:20\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (09:45 - 10:55), CGK - MES (14:00 - 16:20)\",\"duration\":\"7 h 35 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-761\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"09:45\",\"simple_arrival_time\":\"10:55\"},{\"flight_number\":\"JT-384\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"14:00\",\"simple_arrival_time\":\"16:20\"}]}},{\"flight_id\":\"3789709\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-673\\/JT-398\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:45\",\"simple_arrival_time\":\"14:40\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (07:45 - 08:55), CGK - MES (12:20 - 14:40)\",\"duration\":\"7 h 55 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-673\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"07:45\",\"simple_arrival_time\":\"08:55\"},{\"flight_number\":\"JT-398\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"12:20\",\"simple_arrival_time\":\"14:40\"}]}},{\"flight_id\":\"3789703\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-763\\/JT-200\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"12:10\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (06:00 - 07:10), CGK - MES (09:50 - 12:10)\",\"duration\":\"7 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-763\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"07:10\"},{\"flight_number\":\"JT-200\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"09:50\",\"simple_arrival_time\":\"12:10\"}]}},{\"flight_id\":\"3789702\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-763\\/JT-214\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"11:40\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (06:00 - 07:10), CGK - MES (09:20 - 11:40)\",\"duration\":\"6 h 40 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-763\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"07:10\"},{\"flight_number\":\"JT-214\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"09:20\",\"simple_arrival_time\":\"11:40\"}]}},{\"flight_id\":\"2765018\",\"airlines_name\":\"SRIWIJAYA\",\"flight_number\":\"SJ-161\\/SJ-014\",\"price_value\":\"1510000.00\",\"timestamp\":\"2013-01-14 16:57:55\",\"price_adult\":\"1510000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"13:20\",\"simple_arrival_time\":\"21:05\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (13:20 - 14:20), CGK - MES (18:50 - 21:05)\",\"duration\":\"8 h 45 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_sriwijaya_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"SJ-161\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"13:20\",\"simple_arrival_time\":\"14:20\"},{\"flight_number\":\"SJ-014\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"18:50\",\"simple_arrival_time\":\"21:05\"}]}},{\"flight_id\":\"3789704\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-763\\/JT-204\",\"price_value\":\"1126500.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1126500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"13:10\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (06:00 - 07:10), CGK - MES (10:50 - 13:10)\",\"duration\":\"8 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-763\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"07:10\"},{\"flight_number\":\"JT-204\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"10:50\",\"simple_arrival_time\":\"13:10\"}]}},{\"flight_id\":\"3789705\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-945\\/JT-911\",\"price_value\":\"1638000.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1638000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:35\",\"simple_arrival_time\":\"12:35\",\"stop\":\"2 Stops\",\"long_via\":\"Bandung (BDO)\",\"full_via\":\"BPN - BDO (07:35 - 09:30), BDO - MES (10:15 - 12:35)\",\"duration\":\"6 h 0 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-945\",\"departure_city\":\"BPN\",\"arrival_city\":\"BDO\",\"simple_departure_time\":\"07:35\",\"simple_arrival_time\":\"09:30\"},{\"flight_number\":\"JT-911\",\"departure_city\":\"BDO\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"10:15\",\"simple_arrival_time\":\"12:35\"}]}},{\"flight_id\":\"3789708\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-763\\/JT-306\",\"price_value\":\"1154000.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1154000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"14:10\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (06:00 - 07:10), CGK - MES (11:50 - 14:10)\",\"duration\":\"9 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-763\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:00\",\"simple_arrival_time\":\"07:10\"},{\"flight_number\":\"JT-306\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"11:50\",\"simple_arrival_time\":\"14:10\"}]}},{\"flight_id\":\"3789706\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-673\\/JT-306\",\"price_value\":\"1154000.00\",\"timestamp\":\"2013-01-14 16:58:00\",\"price_adult\":\"1154000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:45\",\"simple_arrival_time\":\"14:10\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (07:45 - 08:55), CGK - MES (11:50 - 14:10)\",\"duration\":\"7 h 25 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-673\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"07:45\",\"simple_arrival_time\":\"08:55\"},{\"flight_number\":\"JT-306\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"11:50\",\"simple_arrival_time\":\"14:10\"}]}},{\"flight_id\":\"2765017\",\"airlines_name\":\"SRIWIJAYA\",\"flight_number\":\"SJ-161\\/SJ-016\",\"price_value\":\"1440000.00\",\"timestamp\":\"2013-01-14 16:57:55\",\"price_adult\":\"1440000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"13:20\",\"simple_arrival_time\":\"18:45\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"BPN - CGK (13:20 - 14:20), CGK - MES (16:30 - 18:45)\",\"duration\":\"6 h 25 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_sriwijaya_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"SJ-161\",\"departure_city\":\"BPN\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"13:20\",\"simple_arrival_time\":\"14:20\"},{\"flight_number\":\"SJ-016\",\"departure_city\":\"CGK\",\"arrival_city\":\"MES\",\"simple_departure_time\":\"16:30\",\"simple_arrival_time\":\"18:45\"}]}}]},\"returns\":{\"result\":[{\"flight_id\":\"724202\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-972\\/JT-730\",\"price_value\":\"1357500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1357500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"12:55\",\"simple_arrival_time\":\"20:35\",\"stop\":\"2 Stops\",\"long_via\":\"Surabaya (SUB)\",\"full_via\":\"MES - SUB (12:55 - 17:05), SUB - BPN (18:05 - 20:35)\",\"duration\":\"6 h 40 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-972\",\"departure_city\":\"MES\",\"arrival_city\":\"SUB\",\"simple_departure_time\":\"12:55\",\"simple_arrival_time\":\"17:05\"},{\"flight_number\":\"JT-730\",\"departure_city\":\"SUB\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"18:05\",\"simple_arrival_time\":\"20:35\"}]}},{\"flight_id\":\"724201\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-397\\/JT-766\",\"price_value\":\"1110000.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1110000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:50\",\"simple_arrival_time\":\"19:15\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (07:50 - 10:15), CGK - BPN (16:10 - 19:15)\",\"duration\":\"10 h 25 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-397\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"07:50\",\"simple_arrival_time\":\"10:15\"},{\"flight_number\":\"JT-766\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"16:10\",\"simple_arrival_time\":\"19:15\"}]}},{\"flight_id\":\"724200\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-395\\/JT-766\",\"price_value\":\"1082500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1082500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"11:00\",\"simple_arrival_time\":\"19:15\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (11:00 - 13:25), CGK - BPN (16:10 - 19:15)\",\"duration\":\"7 h 15 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-395\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"11:00\",\"simple_arrival_time\":\"13:25\"},{\"flight_number\":\"JT-766\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"16:10\",\"simple_arrival_time\":\"19:15\"}]}},{\"flight_id\":\"5085425\",\"airlines_name\":\"SRIWIJAYA\",\"flight_number\":\"SJ-017\\/SJ-160\",\"price_value\":\"1360000.00\",\"timestamp\":\"2013-01-11 10:08:19\",\"price_adult\":\"1360000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"19:30\",\"simple_arrival_time\":\"09:10\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (19:30 - 21:50), CGK - BPN (06:10 - 09:10)\",\"duration\":\"12 h 40 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_sriwijaya_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"SJ-017\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"19:30\",\"simple_arrival_time\":\"21:50\"},{\"flight_number\":\"SJ-160\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"06:10\",\"simple_arrival_time\":\"09:10\"}]}},{\"flight_id\":\"5085424\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-960\\/JT-940\",\"price_value\":\"1396000.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1396000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"09:00\",\"simple_arrival_time\":\"20:10\",\"stop\":\"2 Stops\",\"long_via\":\"Bandung (BDO)\",\"full_via\":\"MES - BDO (09:00 - 11:20), BDO - BPN (16:10 - 20:10)\",\"duration\":\"10 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-960\",\"departure_city\":\"MES\",\"arrival_city\":\"BDO\",\"simple_departure_time\":\"09:00\",\"simple_arrival_time\":\"11:20\"},{\"flight_number\":\"JT-940\",\"departure_city\":\"BDO\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"16:10\",\"simple_arrival_time\":\"20:10\"}]}},{\"flight_id\":\"5085423\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-902\\/JT-940\",\"price_value\":\"1451000.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1451000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"13:15\",\"simple_arrival_time\":\"20:10\",\"stop\":\"2 Stops\",\"long_via\":\"Bandung (BDO)\",\"full_via\":\"MES - BDO (13:15 - 15:35), BDO - BPN (16:10 - 20:10)\",\"duration\":\"5 h 55 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-902\",\"departure_city\":\"MES\",\"arrival_city\":\"BDO\",\"simple_departure_time\":\"13:15\",\"simple_arrival_time\":\"15:35\"},{\"flight_number\":\"JT-940\",\"departure_city\":\"BDO\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"16:10\",\"simple_arrival_time\":\"20:10\"}]}},{\"flight_id\":\"724199\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-301\\/JT-766\",\"price_value\":\"1082500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1082500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"10:00\",\"simple_arrival_time\":\"19:15\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (10:00 - 12:25), CGK - BPN (16:10 - 19:15)\",\"duration\":\"8 h 15 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-301\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"10:00\",\"simple_arrival_time\":\"12:25\"},{\"flight_number\":\"JT-766\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"16:10\",\"simple_arrival_time\":\"19:15\"}]}},{\"flight_id\":\"724198\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-207\\/JT-766\",\"price_value\":\"1027500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1027500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"08:40\",\"simple_arrival_time\":\"19:15\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (08:40 - 11:05), CGK - BPN (16:10 - 19:15)\",\"duration\":\"9 h 35 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-207\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"08:40\",\"simple_arrival_time\":\"11:05\"},{\"flight_number\":\"JT-766\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"16:10\",\"simple_arrival_time\":\"19:15\"}]}},{\"flight_id\":\"724192\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-397\\/JT-764\",\"price_value\":\"1110000.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1110000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:50\",\"simple_arrival_time\":\"15:55\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (07:50 - 10:15), CGK - BPN (12:50 - 15:55)\",\"duration\":\"7 h 5 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-397\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"07:50\",\"simple_arrival_time\":\"10:15\"},{\"flight_number\":\"JT-764\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"12:50\",\"simple_arrival_time\":\"15:55\"}]}},{\"flight_id\":\"724191\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-381\\/JT-764\",\"price_value\":\"1082500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1082500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:45\",\"simple_arrival_time\":\"15:55\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (06:45 - 09:10), CGK - BPN (12:50 - 15:55)\",\"duration\":\"8 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-381\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:45\",\"simple_arrival_time\":\"09:10\"},{\"flight_number\":\"JT-764\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"12:50\",\"simple_arrival_time\":\"15:55\"}]}},{\"flight_id\":\"724190\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-211\\/JT-764\",\"price_value\":\"1027500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1027500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"05:45\",\"simple_arrival_time\":\"15:55\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (05:45 - 08:10), CGK - BPN (12:50 - 15:55)\",\"duration\":\"9 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-211\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"05:45\",\"simple_arrival_time\":\"08:10\"},{\"flight_number\":\"JT-764\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"12:50\",\"simple_arrival_time\":\"15:55\"}]}},{\"flight_id\":\"724189\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-381\\/JT-756\",\"price_value\":\"1082500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1082500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:45\",\"simple_arrival_time\":\"15:00\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (06:45 - 09:10), CGK - BPN (11:55 - 15:00)\",\"duration\":\"7 h 15 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-381\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:45\",\"simple_arrival_time\":\"09:10\"},{\"flight_number\":\"JT-756\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"11:55\",\"simple_arrival_time\":\"15:00\"}]}},{\"flight_id\":\"724193\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-207\\/JT-768\",\"price_value\":\"1027500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1027500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"08:40\",\"simple_arrival_time\":\"17:50\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (08:40 - 11:05), CGK - BPN (14:45 - 17:50)\",\"duration\":\"8 h 10 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-207\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"08:40\",\"simple_arrival_time\":\"11:05\"},{\"flight_number\":\"JT-768\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"14:45\",\"simple_arrival_time\":\"17:50\"}]}},{\"flight_id\":\"724194\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-301\\/JT-768\",\"price_value\":\"1082500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1082500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"10:00\",\"simple_arrival_time\":\"17:50\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (10:00 - 12:25), CGK - BPN (14:45 - 17:50)\",\"duration\":\"6 h 50 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-301\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"10:00\",\"simple_arrival_time\":\"12:25\"},{\"flight_number\":\"JT-768\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"14:45\",\"simple_arrival_time\":\"17:50\"}]}},{\"flight_id\":\"724197\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-970\\/JT-366\",\"price_value\":\"1313500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1313500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:00\",\"simple_arrival_time\":\"17:15\",\"stop\":\"2 Stops\",\"long_via\":\"Surabaya (SUB)\",\"full_via\":\"MES - SUB (07:00 - 11:10), SUB - BPN (14:45 - 17:15)\",\"duration\":\"9 h 15 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-970\",\"departure_city\":\"MES\",\"arrival_city\":\"SUB\",\"simple_departure_time\":\"07:00\",\"simple_arrival_time\":\"11:10\"},{\"flight_number\":\"JT-366\",\"departure_city\":\"SUB\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"14:45\",\"simple_arrival_time\":\"17:15\"}]}},{\"flight_id\":\"724196\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-397\\/JT-768\",\"price_value\":\"1110000.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1110000.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"07:50\",\"simple_arrival_time\":\"17:50\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (07:50 - 10:15), CGK - BPN (14:45 - 17:50)\",\"duration\":\"9 h 0 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-397\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"07:50\",\"simple_arrival_time\":\"10:15\"},{\"flight_number\":\"JT-768\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"14:45\",\"simple_arrival_time\":\"17:50\"}]}},{\"flight_id\":\"724195\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-381\\/JT-768\",\"price_value\":\"1082500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1082500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"06:45\",\"simple_arrival_time\":\"17:50\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (06:45 - 09:10), CGK - BPN (14:45 - 17:50)\",\"duration\":\"10 h 5 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-381\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"06:45\",\"simple_arrival_time\":\"09:10\"},{\"flight_number\":\"JT-768\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"14:45\",\"simple_arrival_time\":\"17:50\"}]}},{\"flight_id\":\"724188\",\"airlines_name\":\"LION\",\"flight_number\":\"JT-211\\/JT-756\",\"price_value\":\"1027500.00\",\"timestamp\":\"2013-01-11 10:08:26\",\"price_adult\":\"1027500.00\",\"price_child\":\"0.00\",\"price_infant\":\"0.00\",\"simple_departure_time\":\"05:45\",\"simple_arrival_time\":\"15:00\",\"stop\":\"1 Stop\",\"long_via\":\"Jakarta (CGK)\",\"full_via\":\"MES - CGK (05:45 - 08:10), CGK - BPN (11:55 - 15:00)\",\"duration\":\"8 h 15 m\",\"image\":\"http:\\/\\/www.sandbox.tiket.com\\/images\\/tiket2\\/icon_lion_2.jpg\",\"flight_infos\":{\"flight_info\":[{\"flight_number\":\"JT-211\",\"departure_city\":\"MES\",\"arrival_city\":\"CGK\",\"simple_departure_time\":\"05:45\",\"simple_arrival_time\":\"08:10\"},{\"flight_number\":\"JT-756\",\"departure_city\":\"CGK\",\"arrival_city\":\"BPN\",\"simple_departure_time\":\"11:55\",\"simple_arrival_time\":\"15:00\"}]}}]},\"nearby_go_date\":{\"nearby\":[{\"date\":\"2013-01-31\",\"price\":\"1000000.00\"},{\"date\":\"2013-02-01\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-02\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-03\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-04\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-05\",\"price\":\"1126500.00\"},{\"date\":\"2013-02-06\",\"price\":\"1126500.00\"},{\"date\":\"2013-02-07\",\"price\":\"1209000.00\"},{\"date\":\"2013-02-08\",\"price\":\"1374000.00\"},{\"date\":\"2013-02-09\",\"price\":\"1319000.00\"},{\"date\":\"2013-02-10\",\"price\":\"1027500.00\"}]},\"nearby_ret_date\":{\"nearby\":[{\"date\":\"2013-02-05\",\"price\":\"1126500.00\"},{\"date\":\"2013-02-06\",\"price\":\"1126500.00\"},{\"date\":\"2013-02-07\",\"price\":\"1209000.00\"},{\"date\":\"2013-02-08\",\"price\":\"1374000.00\"},{\"date\":\"2013-02-09\",\"price\":\"1319000.00\"},{\"date\":\"2013-02-10\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-11\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-12\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-13\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-14\",\"price\":\"1027500.00\"},{\"date\":\"2013-02-15\",\"price\":\"1027500.00\"}]},\"token\":\"7f6ba5da47c3a36159463ddddfa530ab\"}\n";
                            JSONObject testResponse = new JSONObject(test);

                            if (!testResponse.isNull("departures")) {
                                Log.d("TRUE", "there is flight data");
                                JSONObject results = testResponse.getJSONObject("departures");
                                JSONArray departure = results.getJSONArray("result");
                                retrieveFlights(start, end, departure);
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

    public void retrieveFlights (String startLoc, String endLoc, JSONArray departure)
            throws JSONException {
        // TODO retrieve flight data
        Log.d("SUCCESS", "Correct response accepted");

        HashMap<String, ArrayList<String>> flightConnect = new HashMap<>();

        for (int i = 0; i < departure.length(); i++) {
            JSONObject flight = (JSONObject) departure.get(i);

            // TODO Assume that only show flights without transit
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
        completeFlightData(startLoc, endLoc, flightConnect);
    }

    public void completeFlightData (String startLoc, String endLoc,
                                    HashMap<String, ArrayList<String>> map) {

        Log.d("FILL", "Start filling list");
        ArrayList<String> keyArray = new ArrayList<>();

        for (String key : map.keySet()) {
            keyArray.add(key);
        }

        ArrayList<Flight> availableFlights = new ArrayList<>();

        for (int i = 0; i < map.size(); i++) {
            String key = keyArray.get(i);

            ArrayList<String> moreFlightInfo = map.get(key);
            // airlineName, price, flightNumber, departTime, arriveTime
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
        FlightAdapter adapter = new FlightAdapter(this, availableFlights);
        listTravel.setAdapter(adapter);
        Log.d("DONE", "ListView populated");

    }

    public void checkUpdate (final String token, final String start, final String end,
                             final String depart) {
        // TODO check update
        // Request a string response from the provided URL.

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
                                getFlightData(token, start, end, depart);
                            }
                            else {
                                Log.d("UPDATE", "update fail");
                                countUpdate = 0;
                            }

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

    public boolean validate() {
        boolean valid = true;

        String startLoc = origin.getText().toString();
        String endLoc = destination.getText().toString();
        String startDay = departureDay.getText().toString();
        String startMonth = departureMonth.getText().toString();
        String startYear = departureYear.getText().toString();

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

        else if ((startDay.isEmpty() && startDay.length() != 2) && (startMonth.isEmpty()
                && startMonth.length() != 2) && (startYear.isEmpty() && startYear.length() != 4)) {
            Toast.makeText(BookPlaneActivity.this, "Please write the departure date",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }
}
