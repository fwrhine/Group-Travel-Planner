package com.example.pplki18.grouptravelplanner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.example.pplki18.grouptravelplanner.utils.Event;
import com.example.pplki18.grouptravelplanner.utils.Train;
import com.example.pplki18.grouptravelplanner.utils.TrainAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class BookTrainFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference planRef;
    DatabaseReference eventRef;
    StorageReference storageReference;

    private AutoCompleteTextView origin, destination;
    private Button searchButton;

    private String plan_id;

    private String date;
    private String startDate;

    private String token;
    private HashMap<String, String> availableStations;

    private RequestQueue queue;
    private ListView listTravel;

    private String prevActivity;
    private List<String> eventIDs = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_book_train, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        eventRef = firebaseDatabase.getReference().child("events");
        storageReference = FirebaseStorage.getInstance().getReference();

        origin = Objects.requireNonNull(getView()).findViewById(R.id.origin);
        destination = getView().findViewById(R.id.destination);

        searchButton = getView().findViewById(R.id.searchButton);
        listTravel = getView().findViewById(R.id.listTravel);

        plan_id = getArguments().getString("plan_id");

        date = getArguments().getString("date");

        token = "";
        queue = Volley.newRequestQueue(this.getActivity());

        availableStations = new HashMap<>();

        prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");
        if (prevActivity.equals("CreateNewPlanActivity")) {
            events = getActivity().getIntent().getParcelableArrayListExtra("events");
        }

        initSearch();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                String prevActivity = data.getStringExtra("ACTIVITY");
                if (prevActivity != null && prevActivity.equals("EditPlanActivity")) {
                    getActivity().finish();
                } else {
                    events = data.getParcelableArrayListExtra("events");

                    for (Event e : events) {
                        Log.d("TITLE", e.getTitle());
                    }

                    Intent intent = new Intent(getActivity(), CreateNewPlanActivity.class);
                    intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                }
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initSearch() {

        String secretKey = "6c484049beacda6541bf40c90e62e8e5";
        String tokenUrl = "https://api-sandbox.tiket.com/apiv1/payexpress"
                + "?method=getToken&secretkey=" + secretKey + "&output=json";

        generateToken(tokenUrl, new BookTrainFragment.TokenCallback() {
            @Override
            public void onCallback(String tokenStr) {
                token = tokenStr;

                fillStationList(new BookTrainFragment.StationCallback() {
                    @Override
                    public void onCallback(HashMap<String, String> map) {
                        Log.d("MAP-FINAL", "map size: "+ map.size());
                        availableStations = map;

                        String[] stationKeyArray = availableStations.keySet().toArray(new String[availableStations.size()]);
                        ArrayList<String> stationArrayList = new ArrayList<>();

                        for (int i = 0; i < availableStations.size(); i++) {
                            String currentKey = stationKeyArray[i];
                            stationArrayList.add(availableStations.get(currentKey)+" | "+currentKey);
                        }

                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(BookTrainFragment.this.getActivity(), R.layout.support_simple_spinner_dropdown_item, stationArrayList);
                        origin.setAdapter(adapter1);
                        destination.setAdapter(adapter1);

                        origin.setText(stationArrayList.get(0));
                        destination.setText(stationArrayList.get(1));
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

        Objects.requireNonNull(getView()).findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        searchButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if(validate()) {

                            String[] startSplit = origin.getText().toString().split(" \\| ");
                            String startLoc = startSplit[1];

                            Log.d("START", startLoc);

                            String[] endSplit = destination.getText().toString().split(" \\| ");
                            String endLoc = endSplit[1];

                            Log.d("START", startLoc);
                            final String departDate = startDate;

                            if (availableStations.containsKey(startLoc)) {

                                if (availableStations.containsKey(endLoc)) {

                                    final String start = availableStations.get(startLoc);
                                    final String end = availableStations.get(endLoc);
                                    getView().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                                    trainApiCall(token, start, end, departDate);
                                } else {
                                    Toast.makeText(BookTrainFragment.this.getActivity()
                                            , "No Stations are in the arrival area"
                                            , Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(BookTrainFragment.this.getActivity()
                                        , "No Stations are in the departure area"
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

    private void fillStationList(final StationCallback callback) {
        String url = "https://api-sandbox.tiket.com/train_api/train_station?token="+ token +"&output=json";

        Log.d("FILL-MAP", "url -> " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("FILL-MAP", "start -> " + response);
                            JSONObject json = new JSONObject(response);

                            JSONObject allStations = json.getJSONObject("stations");
                            JSONArray stationList = allStations.getJSONArray("station");

                            HashMap<String, String> stationMap = new HashMap<>();

                            for (int i = 0; i < stationList.length(); i++) {

                                JSONObject stationData = stationList.getJSONObject(i);

                                String stationLoc = stationData.getString("station_name");
                                String stationCode = stationData.getString("station_code");

                                stationMap.put(stationLoc, stationCode);
                            }
                            availableStations = stationMap;

                            callback.onCallback(availableStations);

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

    @SuppressWarnings("SpellCheckingInspection")
    private void trainApiCall (final String token, final String start, final String end,
                                final String depart) {
        // Request a string response from the provided URL.
        //final String url = "http://api-sandbox.tiket.com/search/train?d="
          //      + start + "&a=" + end + "&date=" + depart + "&token=" + token + "&output=json";

        final String url = "https://api-sandbox.tiket.com/search/train?d=" + start + "&a=" + end
                + "&date=" + depart + "&ret_date=&adult=1&child=0&class=all&token=" + token
                + "&output=json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // JSONObject json = new JSONObject(response); [SHOULD BE USED WHEN API IS FIXED]
                            Log.d("JSON", "there is response: " + response);
                            Log.d("URL", url);

                            // FOR TESTING ONLY
                            String test = "{\"output_type\":\"json\",\"diagnostic\":{\"elapstime\":\"0.5343\",\"memoryusage\":\"5.16MB\",\"status\":\"200\",\"lang\":\"en\",\"currency\":\"IDR\"},\"search_queries\":{\"dep_station\":\"GMR\",\"arr_station\":\"BD\",\"date\":\"2012-06-03\",\"formatted_date\":\"03 June 2012\",\"train_class\":\"all\",\"count_adult\":\"1\",\"count_child\":\"0\",\"count_infant\":\"0\"},\"departures\":{\"result\":[{\"detail_availability\":\"90\",\"schedule_id\":\"3\",\"train_id\":\"IVHAN3\",\"train_name\":\"Argo Cantik\",\"departure_time\":\"17:00\",\"arrival_time\":\"19:30\",\"class_name\":\"bis\",\"subclass_name\":\"B\",\"is_promo\":0,\"price_adult\":\"56.000,00\",\"price_child\":\"46.000,00\",\"duration\":\"2 h 30 m\",\"price_infant\":\"3.000,00\"}]},\"token\":\"ebaa92ff1a060a7a5610b7159bd68b28\"}\n";

                            JSONObject testResponse = new JSONObject(test);

                            if (!testResponse.isNull("departures")) {
                                Log.d("TRUE", "there is train data");
                                JSONObject results = testResponse.getJSONObject("departures");
                                JSONArray departure = results.getJSONArray("result");
                                getTrainData(start, end, departure);
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
            public void retry(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getTrainData (String startLoc, String endLoc, JSONArray departure)
            throws JSONException {

        Log.d("SUCCESS", "Correct response accepted");

        HashMap<String, ArrayList<String>> trainConnect = new HashMap<>();

        for (int i = 0; i < departure.length(); i++) {
            JSONObject train = (JSONObject) departure.get(i);

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
        setTrainList(startLoc, endLoc, trainConnect);
    }

    private void setTrainList (final String startLoc, final String endLoc,
                                HashMap<String, ArrayList<String>> map) {

        Log.d("FILL", "Start filling list");

        ArrayList<String> keyArray = new ArrayList<>(map.keySet());

        ArrayList<Train> availableTrains = new ArrayList<>();

        for (int i = 0; i < map.size(); i++) {
            String key = keyArray.get(i);

            ArrayList<String> moreTrainInfo = map.get(key);

            String[] splitPrice = moreTrainInfo.get(1).split(",");
            String[] addCommaPrice = splitPrice[0].split("");

            StringBuilder editedPrice = new StringBuilder();

            for (String anAddCommaPrice : addCommaPrice) {
                if (anAddCommaPrice.equals(".")) {
                    editedPrice.append(",");
                } else {
                    editedPrice.append(anAddCommaPrice);
                }
            }

            String trainName = moreTrainInfo.get(0);
            String priceValue = "Rp " + editedPrice.toString();
            String departTime = moreTrainInfo.get(2);
            String arriveTime = moreTrainInfo.get(3);

            //Train train = new Train(trainName, departTime, arriveTime,
              //      priceValue, startLoc, endLoc);
            Train train = new Train();

            train.setTrainName(trainName);
            train.setDepartureCity(startLoc);
            train.setArrivalCity(endLoc);
            train.setDepartureTime(departTime);
            train.setArrivalTime(arriveTime);
            train.setPrice(priceValue);

            availableTrains.add(train);
        }

        Log.d("ALMOST", "Almost finished");
        TrainAdapter adapter = new TrainAdapter(this.getActivity(), availableTrains);

        Objects.requireNonNull(getView()).findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        listTravel.setAdapter(adapter);
        listTravel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View viewItem, int i, long l) {

                final Dialog dialog = new Dialog(BookTrainFragment.this.getActivity());

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

                                final TextView tvDepartTime = viewItem.findViewById(R.id.departTime);
                                final TextView tvArriveTime = viewItem.findViewById(R.id.arriveTime);

                                // Add for Airline Name & Price
                                final TextView tvTrainName = viewItem.findViewById(R.id.trainName);
                                final TextView tvPrice = viewItem.findViewById(R.id.price);

                                // For the city name located in the input box
                                String startLoc = origin.getText().toString();
                                String endLoc = destination.getText().toString();

                                String departTime = tvDepartTime.getText().toString();
                                String arriveTime = tvArriveTime.getText().toString();
                                String price = tvPrice.getText().toString();

                                if (prevActivity.equals("CreateNewPlanActivity")) {
                                    Event anEvent = new Event();
                                    anEvent.setTitle("Train");
                                    anEvent.setDescription("Train for Transport");
                                    anEvent.setDate(date);
                                    anEvent.setType("trains");

                                    anEvent.setOrigin(startLoc);
                                    anEvent.setDestination(endLoc);
                                    anEvent.setDeparture_time(departTime);
                                    anEvent.setArrival_time(arriveTime);
                                    anEvent.setTransport_number(tvTrainName.getText().toString());
                                    anEvent.setPrice(price);

                                    events.add(anEvent);
                                    Intent intent = new Intent(getActivity(), CreateNewPlanActivity.class);
                                    intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

                                    getActivity().setResult(RESULT_OK, intent);
                                    getActivity().finish();

                                } else {

//                                    DatabaseHelper myDb = new DatabaseHelper(BookTrainFragment.this.getActivity());
//                                    SQLiteDatabase db = myDb.getReadableDatabase();
//
//                                    final String date = startDate;
//
//                                    ContentValues values = new ContentValues();
//
//                                    values.put(EventContract.EventEntry.COL_PLAN_ID, plan_id);
//                                    values.put(EventContract.EventEntry.COL_TITLE, "Train");
//                                    values.put(EventContract.EventEntry.COL_DESCRIPTION, "Train for Transport");
//                                    values.put(EventContract.EventEntry.COL_DATE, date);
//                                    values.put(EventContract.EventEntry.COL_TYPE, "trains");
//                                    values.put(EventContract.EventEntry.COL_PRICE, price);
//
//                                    values.put(EventContract.EventEntry.COL_ORIGIN, startLoc);
//                                    values.put(EventContract.EventEntry.COL_DESTINATION, endLoc);
//                                    values.put(EventContract.EventEntry.COL_DEPARTURE_TIME, departTime);
//                                    values.put(EventContract.EventEntry.COL_ARRIVAL_TIME, arriveTime);
//                                    values.put(EventContract.EventEntry.COL_TRANS_NUMBER, tvTrainName.getText().toString());
//
//                                    long newRowId = db.insert(EventContract.EventEntry.TABLE_NAME, null, values);
//
//                                    if (newRowId != -1) {
//                                        Toast.makeText(BookTrainFragment.this.getActivity(), "Selected Train : "
//                                                + tvTrainName.getText().toString(), Toast.LENGTH_LONG).show();
//                                        dialog.dismiss();
//                                        getActivity().finish();
//                                    }
//
//                                    else {
//                                        Toast.makeText(BookTrainFragment.this.getActivity(),
//                                                "Selection Failed!", Toast.LENGTH_LONG).show();
//                                        dialog.dismiss();
//                                    }

                                    Event anEvent = new Event("Train", date, departTime, arriveTime, "trains");
                                    anEvent.setPlan_id(plan_id);
                                    anEvent.setDescription("Train for Transport");
                                    anEvent.setPrice(price);
                                    anEvent.setOrigin(startLoc);
                                    anEvent.setDestination(endLoc);
                                    anEvent.setTransport_number(tvTrainName.getText().toString());

                                    saveEventToPlan(anEvent);
                                    getActivity().finish();
                                }
                            }
                        }
                );
            }
        });
        Log.d("DONE", "ListView populated");

    }

    private void saveEventToPlan(Event anEvent) {
        final String eventId = eventRef.push().getKey();
        anEvent.setEvent_id(eventId);
        anEvent.setPlan_id(plan_id);
        anEvent.setCreator_id(firebaseUser.getUid());
        eventRef.child(eventId).setValue(anEvent);

        planRef = firebaseDatabase.getReference().child("plans").child(plan_id).child("events");
        getAllEventIDs(new EventIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                eventIDs = list;
                eventIDs.add(eventId);

                planRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        planRef.setValue(eventIDs);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void getAllEventIDs(final EventIdCallback userIdCallback){
        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String eventId = postSnapshot.getValue(String.class); // String of groupID
                    eventIDs.add(eventId);
                }
                userIdCallback.onCallback(eventIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface EventIdCallback{
        void onCallback(List<String> list);
    }

    private boolean validate() {
        boolean valid = true;

        String startLoc = origin.getText().toString();
        String endLoc = destination.getText().toString();

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

        return valid;
    }

    private interface StationCallback {
        void onCallback (HashMap<String, String> map);
    }

    private interface TokenCallback {
        void onCallback (String tokenStr);
    }
}
