package com.example.pplki18.grouptravelplanner;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.pplki18.grouptravelplanner.utils.Train;
import com.example.pplki18.grouptravelplanner.utils.TrainAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BookTrainFragment extends Fragment {

    EditText origin, destination;
    EditText departureDay, departureMonth, departureYear;
    Button searchButton;

    String token;

    RequestQueue queue;
    int countUpdate;
    ListView listTravel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_book_train, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        origin = (EditText) getView().findViewById(R.id.origin);
        destination = (EditText) getView().findViewById(R.id.destination);

        departureDay = (EditText) getView().findViewById(R.id.departureDay);
        departureMonth = (EditText) getView().findViewById(R.id.departureMonth);
        departureYear = (EditText) getView().findViewById(R.id.departureYear);

        searchButton = (Button) getView().findViewById(R.id.searchButton);
        listTravel = (ListView) getView().findViewById(R.id.listTravel);

        token = "";
        countUpdate = 0;
        queue = Volley.newRequestQueue(this.getActivity());

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
                        final String depart = departureYear.getText().toString()
                                + "-" + departureMonth.getText().toString() + "-"
                                + departureDay.getText().toString();

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
                            getTrainData(token, start, end, depart);
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

    public void getTrainData(final String token, final String start, final String end,
                              final String depart) {
        // Request a string response from the provided URL.

        final String url = "http://api-sandbox.tiket.com/search/train?d="
                + start + "&a=" + end + "&date=" + depart + "&token=" + token + "&output=json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            Log.d("JSON", "there is response: " + response);
                            Log.d("URL", url);

                            //String test = "{\"output_type\":\"json\",\"diagnostic\":{\"elapstime\":\"0.5343\",\"memoryusage\":\"5.16MB\",\"status\":\"200\",\"lang\":\"en\",\"currency\":\"IDR\"},\"search_queries\":{\"dep_station\":\"GMR\",\"arr_station\":\"BD\",\"date\":\"2012-06-03\",\"formatted_date\":\"03 June 2012\",\"train_class\":\"all\",\"count_adult\":\"1\",\"count_child\":\"0\",\"count_infant\":\"0\"},\"departures\":{\"result\":[{\"detail_availability\":\"90\",\"schedule_id\":\"3\",\"train_id\":\"IVHAN3\",\"train_name\":\"Argo Cantik\",\"departure_time\":\"17:00\",\"arrival_time\":\"19:30\",\"class_name\":\"bis\",\"subclass_name\":\"B\",\"is_promo\":0,\"price_adult\":\"56.000,00\",\"price_child\":\"46.000,00\",\"duration\":\"2 h 30 m\",\"price_infant\":\"3.000,00\"}]},\"token\":\"ebaa92ff1a060a7a5610b7159bd68b28\"}\n";

                            //JSONObject testResponse = new JSONObject(test);

                            if (!json.isNull("departures")) {
                                Log.d("TRUE", "there is train data");
                                JSONObject results = json.getJSONObject("departures");
                                JSONArray departure = results.getJSONArray("result");
                                retrieveTrains(start, end, departure);
                            }

                            else {
                                Log.d("FALSE", "there is no train data");
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

    public void retrieveTrains (String startLoc, String endLoc, JSONArray departure)
            throws JSONException {
        // TODO retrieve flight data
        Log.d("SUCCESS", "Correct response accepted");

        HashMap<String, ArrayList<String>> trainConnect = new HashMap<>();

        for (int i = 0; i < departure.length(); i++) {
            JSONObject train = (JSONObject) departure.get(i);

            // TODO Assume that only show flights without transit
            String id = train.getString("train_id");
            String name = train.getString("train_name");
            String price = train.getString("price_adult");
            String departTime = train.getString("departure_time");
            String arriveTime = train.getString("arrival_time");

            ArrayList<String> tempList = new ArrayList<>();
            tempList.add(name);
            tempList.add(price);
            tempList.add(departTime);
            tempList.add(arriveTime);

            trainConnect.put(id, tempList);

        }

        Log.d("TRAIN-LIST", "List size: " + trainConnect.size());
        completeFlightData(startLoc, endLoc, trainConnect);
    }

    public void completeFlightData (String startLoc, String endLoc,
                                    HashMap<String, ArrayList<String>> map) {

        Log.d("FILL", "Start filling list");
        ArrayList<String> keyArray = new ArrayList<>();

        for (String key : map.keySet()) {
            keyArray.add(key);
        }

        ArrayList<Train> availableTrains = new ArrayList<>();

        for (int i = 0; i < map.size(); i++) {
            String key = keyArray.get(i);

            ArrayList<String> moreTrainInfo = map.get(key);

            String airlineName = moreTrainInfo.get(0);
            String priceValue = "IDR " + moreTrainInfo.get(1);
            String departTime = moreTrainInfo.get(2);
            String arriveTime = moreTrainInfo.get(3);

            Train train = new Train(airlineName, departTime, arriveTime,
                    priceValue, startLoc, endLoc);
            availableTrains.add(train);
        }

        Log.d("ALMOST", "Almost finished");
        TrainAdapter adapter = new TrainAdapter(this.getActivity(), availableTrains);
        listTravel.setAdapter(adapter);
        Log.d("DONE", "ListView populated");

    }

    public boolean validate() {
        boolean valid = true;

        String startLoc = origin.getText().toString();
        String endLoc = destination.getText().toString();
        String startDay = departureDay.getText().toString();
        String startMonth = departureMonth.getText().toString();
        String startYear = departureYear.getText().toString();

        if (startLoc.isEmpty()) {
            Toast.makeText(BookTrainFragment.this.getActivity(), "Please write the origin",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if (endLoc.isEmpty()) {
            Toast.makeText(BookTrainFragment.this.getActivity(), "Please write the destination",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if ((startDay.isEmpty() && startDay.length() != 2) && (startMonth.isEmpty()
                && startMonth.length() != 2) && (startYear.isEmpty() && startYear.length() != 4)) {
            Toast.makeText(BookTrainFragment.this.getActivity(), "Please write the departure date",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }
}
