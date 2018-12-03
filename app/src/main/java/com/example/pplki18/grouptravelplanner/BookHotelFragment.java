package com.example.pplki18.grouptravelplanner;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.pplki18.grouptravelplanner.data.Event;
import com.example.pplki18.grouptravelplanner.utils.HtmlParser;
import com.example.pplki18.grouptravelplanner.data.Hotel;
import com.example.pplki18.grouptravelplanner.utils.PaginationScrollListener;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Hotel;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.example.pplki18.grouptravelplanner.utils.VolleyResponseListener;
import com.example.pplki18.grouptravelplanner.utils.VolleyUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.pplki18.grouptravelplanner.utils.HtmlParser.parseHotel;
import static com.example.pplki18.grouptravelplanner.utils.HtmlParser.parseHotelList;

public class BookHotelFragment extends Fragment {
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference planRef;
    DatabaseReference eventRef;
    StorageReference storageReference;

    private RecyclerView recyclerViewPlace;
    private LinearLayoutManager linearLayoutManager;
    private RVAdapter_Hotel adapter;
    private VolleyUtils volleyUtils;
    private SessionManager sessionManager;

    private TextView connectionText;
    private ProgressBar progressBar;

    //info view
    private ConstraintLayout infoView;
    private ImageView search;
    private ImageView sort;
    private TextView infoDate;
    private TextView infoGuest;
    private TextView infoRoom;

    //close search view
    private ConstraintLayout closeSearchView;
    private ImageView close;

    private Dialog searchDialog;

    //info dialog
    private Dialog infoDialog;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    //date formatter
    private SimpleDateFormat dateFormatter1;
    private SimpleDateFormat dateFormatter2;
    private SimpleDateFormat dateFormatter3;
    private SimpleDateFormat dateFormatter4;

    private String region;
    private String regionCode;
    private String plan_id;
    private String event_date;

    private String sortBy = "popularity";
    private int sortByInt = 0;
    private Date checkInDate;
    private Date checkOutDate;
    private long nights = 1;
    private String numOfGuest = "1";
    private String numOfRoom = "1";

    private Date checkInDateTemp;
    private Date checkOutDateTemp;
    private long nightsTemp = 1;
    private String numOfGuestTemp = "1";
    private String numOfRoomTemp = "1";

    private String tripadvisorUrl;
    private String nextPageUrl;
    private ArrayList<Hotel> hotels = new ArrayList<>();

    private String prevActivity;
    private List<String> eventIDs = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private String start_time;
    private String end_time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hotel_list, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "!");
        if (requestCode == 3) {
            Log.d("requestCode", "3!");
            if (resultCode == RESULT_OK) {
                Log.d("resultCode", "RESULT_OK!");
                String prevActivity = data.getStringExtra("ACTIVITY");
                if (prevActivity != null && prevActivity.equals("EditPlanActivity")) {
                    getActivity().finish();
                } else {
                    events = data.getParcelableArrayListExtra("events");

                    for (Event e : events) {
                        Log.d("testtt", e.getTitle());
                    }

                    Intent intent = new Intent(getActivity(), CreateNewPlanActivity.class);
                    intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);

                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                }

            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        eventRef = firebaseDatabase.getReference().child("events");
        storageReference = FirebaseStorage.getInstance().getReference();

        init();

        connectionText.setVisibility(View.GONE);
        closeSearchView.setVisibility(View.GONE);

        loadInfoView();

        recyclerViewPlace.setHasFixedSize(true);
        recyclerViewPlace.setLayoutManager(linearLayoutManager);
        recyclerViewPlace.setItemAnimator(new DefaultItemAnimator());

        recyclerViewPlace.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                sendRequest(true);
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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchPopup();
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortPopup();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSearchView.setVisibility(View.GONE);
                infoView.setVisibility(View.VISIBLE);
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);
                isLastPage = false;
                hotels.clear();
                getTripadvisorUrl();
            }
        });

        infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoPopup();
            }
        });

        getTripadvisorUrl();

    }

    private void loadInfoView() {
        String date = dateFormatter3.format(checkInDate) + " - " + dateFormatter3.format(checkOutDate);

        infoDate.setText(date);
        infoGuest.setText(numOfGuest);
        infoRoom.setText(numOfRoom);
    }

    private void showSearchPopup() {
        searchDialog.setContentView(R.layout.search_dialog);
        SearchView searchView = searchDialog.findViewById(R.id.search_hotel);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newQuery) {
                searchHotel(newQuery);
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);
                infoView.setVisibility(View.INVISIBLE);
                closeSearchView.setVisibility(View.VISIBLE);
                searchDialog.dismiss();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                if (TextUtils.isEmpty(newQuery)){
                    getTripadvisorUrl();
                    adapter.clear();
                    progressBar.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        searchDialog.show();
        searchView.requestFocus();
    }

    private void showSortPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sort by");

        // add a radio button list
        String[] sort = {"Popularity", "Recommended", "Lowest price", "Distance"};
        final String[] actualSort = {"popularity", "recommended", "priceLow", "distLow"};
        int checkedItem = sortByInt;
        builder.setSingleChoiceItems(sort, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortBy = actualSort[which];
                sortByInt = which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);
                getTripadvisorUrl();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showInfoPopup() {
        infoDialog.setContentView(R.layout.hotel_dialog);

        // set position
        Window window = infoDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        // set animation
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // elements in infoDialog
        final TextView dialogCheckIn = infoDialog.findViewById(R.id.start_date);
        final TextView dialogCheckOut = infoDialog.findViewById(R.id.end_date);
        final RelativeLayout checkInLayout = infoDialog.findViewById(R.id.check_in);
        final RelativeLayout checkOutLayout = infoDialog.findViewById(R.id.check_out);
        final TextView dialogGuest = infoDialog.findViewById(R.id.guest);
        final TextView dialogRoom = infoDialog.findViewById(R.id.room);
        final Button dialogSearch = infoDialog.findViewById(R.id.search_button);
        final TextView dialogDays = infoDialog.findViewById(R.id.days);

        final Calendar newCalendar = Calendar.getInstance();

        dialogGuest.setText(numOfGuest);
        dialogRoom.setText(numOfRoom);
        dialogCheckIn.setText(dateFormatter1.format(checkInDate));
        dialogCheckOut.setText(dateFormatter1.format(checkOutDate));
        dialogDays.setText(String.valueOf(nights));

        checkInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar curr = Calendar.getInstance();
                    curr.setTime(checkInDateTemp);
                    newCalendar.setTime(dateFormatter1.parse(dialogCheckIn.getText().toString()));
                    newCalendar.set(Calendar.YEAR, curr.get(Calendar.YEAR));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                fromDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        checkInDateTemp = newDate.getTime();
                        dialogCheckIn.setText(dateFormatter1.format(checkInDateTemp));

                        if (!dialogCheckIn.getText().toString().equals("Date") &&
                                !dialogCheckOut.getText().toString().equals("Date")) {
                            long diff = checkOutDateTemp.getTime() - checkInDateTemp.getTime();
                            nightsTemp = (diff / (24 * 60 * 60 * 1000) + 2) - 1;
                            dialogDays.setText(nightsTemp + "");
                        }
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fromDatePickerDialog.show();
            }
        });

        checkOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar curr = Calendar.getInstance();
                    curr.setTime(checkOutDateTemp);
                    newCalendar.setTime(dateFormatter1.parse(dialogCheckOut.getText().toString()));
                    newCalendar.set(Calendar.YEAR, curr.get(Calendar.YEAR));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                toDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        checkOutDateTemp = newDate.getTime();
                        dialogCheckOut.setText(dateFormatter1.format(checkOutDateTemp));

                        if (!dialogCheckIn.getText().toString().equals("Date") &&
                                !dialogCheckOut.getText().toString().equals("Date")) {
                            long diff = checkOutDateTemp.getTime() - checkInDateTemp.getTime();
                            nightsTemp = (diff / (24 * 60 * 60 * 1000) + 1) - 1;
                            dialogDays.setText(nightsTemp + "");
                        }
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                toDatePickerDialog.getDatePicker().setMinDate(checkInDateTemp.getTime());
                toDatePickerDialog.show();

            }
        });

        dialogGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.number_picker_dialog, null);
                final NumberPicker numberPicker = dialogLayout.findViewById(R.id.number_picker);

                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(32);
                numberPicker.setValue(Integer.parseInt(dialogGuest.getText().toString()));

                builder.setView(dialogLayout);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                numOfGuestTemp = String.valueOf(numberPicker.getValue());
                                dialogGuest.setText(numOfGuestTemp);
                            }
                        });

                builder.show();
            }
        });

        dialogRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.number_picker_dialog, null);
                final NumberPicker numberPicker = dialogLayout.findViewById(R.id.number_picker);

                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(8);
                numberPicker.setValue(Integer.parseInt(dialogRoom.getText().toString()));

                builder.setView(dialogLayout);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                numOfRoomTemp = String.valueOf(numberPicker.getValue());
                                dialogRoom.setText(numOfRoomTemp);
                            }
                        });

                builder.show();
            }
        });

        dialogSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numOfGuest = numOfGuestTemp;
                numOfRoom = numOfRoomTemp;
                checkInDate = checkInDateTemp;
                checkOutDate = checkOutDateTemp;
                nights = nightsTemp;
                infoDialog.dismiss();
                loadInfoView();
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);
                getTripadvisorUrl();
            }
        });

        infoDialog.show();
    }

    private void noConnection(VolleyError volleyError) {
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "No internet connection.";
        } else if (volleyError instanceof NoConnectionError) {
            message = "No internet connection.";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection timeout.";
        }

        progressBar.setVisibility(View.GONE);
        connectionText.setVisibility(View.VISIBLE);
        toastMessage(message);
    }

    private void getTripadvisorUrl() {
        String url = "https://www.tripadvisor.com/TypeAheadJson?action=API&startTime=" +
                System.currentTimeMillis()+ "&uiOrigin=GEOSCOPE&source=GEOSCOPE&interleaved=true" +
                "&types=geo,theme_park&neighborhood_geos=true&link_type=hotel&details=true" +
                "&max=12&injectNeighborhoods=true&query=" + region;

        volleyUtils.getRequest(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resp = new JSONObject(response);
                    JSONArray results = resp.optJSONArray("results");
                    JSONObject firstResult = new JSONObject(results.get(0).toString());
                    tripadvisorUrl = "https://www.tripadvisor.com" +
                            firstResult.optString("url");

                    String[] url = tripadvisorUrl.split("-");
                    regionCode = url[1].substring(1);

                    sendRequest(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error) {
                Log.d("ERROR GETTRIPADVISORURL", error.toString());
                noConnection(error);
            }
        });

    }

    private void sendRequest(final boolean isNextPage) {
        String url;
        if (!isNextPage) {
            url = tripadvisorUrl;
        } else {
            url = nextPageUrl;
        }

        //request body
        String requestBody = "{}";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("changeSet", "TRAVEL_INFO");
            jsonBody.put("showSnippets", false);
            jsonBody.put("staydates", dateFormatter4.format(checkInDate) + "_" +
                    dateFormatter4.format(checkOutDate));
            jsonBody.put("uguests", numOfRoom + "_" + numOfGuest);
            jsonBody.put("sortOrder", sortBy);
            requestBody = jsonBody.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html, */*");
        headers.put("Accept-Language", "en-US,en;q=0.5");
        headers.put("Cache-Control", "no-cache");
        headers.put("Connection", "keep-alive");
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.put("Host", "www.tripadvisor.com");
        headers.put("Pragma", "no-cache");
        headers.put("Referer", tripadvisorUrl);
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");

        volleyUtils.postRequest(url, requestBody, headers, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                HtmlParser.HtmlParseResult result = parseHotelList(response);

                // check if last page
                if (result.getNextPage().isEmpty()) {
                    isLastPage = true;
                } else {
                    nextPageUrl = "https://www.tripadvisor.com" + result.getNextPage();
                }

                // check if loading next page
                if (!isNextPage) {
                    populatePlaceRecyclerView(result.getHotels());
                } else {
                    adapter.removeLoadingFooter();
                    isLoading = false;

                    adapter.addAll(result.getHotels());

                    if (!isLastPage) adapter.addLoadingFooter();
                }
            }
            @Override
            public void onError(VolleyError error) {
                Log.d("ERROR HOTEL LIST", error.toString());
            }
        });
    }

    private void searchHotel(String query) {
        String url = "https://www.tripadvisor.com/TypeAheadJson?action=API&query=" +
                query + "&interleaved=true&types=hotel&filter=nobroad&parentids=" +
                regionCode + "&name_depth=1&details=true&legacy_format=true&max=8";

        volleyUtils.getRequest(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("HOTEL SEARCH", response);
                isLastPage = true;
                getHotelsSearch(response, new HotelSearchCallback() {
                    @Override
                    public void onCallback() {
                        populatePlaceRecyclerView(hotels);
                    }
                });
            }
            @Override
            public void onError(VolleyError error) {
                Log.d("ERROR HOTEL SEARCH", error.toString());
                noConnection(error);
            }
        });
    }

    private void getHotelsSearch(String response, final HotelSearchCallback callback) {
//        ArrayList<Hotel> hotels = new ArrayList<>();
        try {
            JSONArray results = new JSONArray(response);

            for (int i = 0 ; i < results.length() ; i++)
            {
                JSONObject hotelObj = new JSONObject(results.get(i).toString());
                JSONObject hotelDetails = hotelObj.optJSONObject("details");

//                Hotel hotel = new Hotel();
//                hotel.setName(hotelDetails.optString("name"));
//                hotel.setRating(hotelDetails.optString("star_rating", "-"));
//                hotel.setAddress(hotelDetails.optString("address"));
                final String url = "https://www.tripadvisor.com/" + hotelObj.optString("url");

                requestHotelDetail(url, new HotelDetailCallback() {
                    @Override
                    public void onCallback(Hotel hotel) {
                        hotel.setHotel_id(url);
                        hotels.add(hotel);
                        progressBar.setVisibility(View.GONE);

                        if (hotels.size() == 2) {
                            callback.onCallback();
                        }
                    }
                });

//                volleyUtils.getRequest(url, new VolleyResponseListener() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("WHAT", "REQUEST HOTEL DETAIL");
//                        hotels.add(parseHotel(response));
////                        progressBar.setVisibility(View.GONE);
//                    }
//                    @Override
//                    public void onError(VolleyError error) {
////                        Log.d(TAG, "HOTEL DETAIL ERROR");
////                        noConnection(error);
//                    }
//                });

//                hotels.add(hotel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestHotelDetail(String url, final HotelDetailCallback callback) {
        volleyUtils.getRequest(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d("WHAT", "REQUEST HOTEL DETAIL");
                Hotel hotel = parseHotel(response);
                callback.onCallback(hotel);
            }
            @Override
            public void onError(VolleyError error) {
                        Log.d("requestHotelDetail", "HOTEL DETAIL ERROR");
                        noConnection(error);
            }
        });
    }

    private void populatePlaceRecyclerView(final List<Hotel> hotels) {
        RVAdapter_Hotel.ClickListener clickListener = new RVAdapter_Hotel.ClickListener() {
            @Override public void cardViewOnClick(View v, int position) {
                Intent intent = new Intent(getActivity(), PlaceActivity.class);
                intent.putExtra("PLACE_ID", String.valueOf(adapter.getAll().get(position).getHotel_id()));
                intent.putExtra("FRAGMENT", "HotelFragment");
                Log.d("FRAGMENT", "HotelFragment");
                intent.putExtra("ACTIVITY", prevActivity);
                intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);
                intent.putExtra("plan_id", plan_id);
                intent.putExtra("checkInDate", dateFormatter2.format(checkInDate));
                intent.putExtra("checkOutDate", dateFormatter2.format(checkOutDate));
                intent.putExtra("type", "hotel");
                startActivityForResult(intent, 3);
            }

            @Override public void addImageOnClick(View v, int position) {
                setTime(adapter.getAll().get(position));
            }
        };
        progressBar.setVisibility(View.GONE);
        adapter.setListener(clickListener);
        adapter.addAll(hotels);

        recyclerViewPlace.setAdapter(adapter);

        if (!isLastPage) adapter.addLoadingFooter();
    }

    private void setTime(final Hotel hotel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getLayoutInflater();
        //TODO HELP
        ViewGroup parent = Objects.requireNonNull(getView()).findViewById(R.id.container);
        View dialogLayout = inflater.inflate(R.layout.set_time_dialog, parent, false);
        TextView start = dialogLayout.findViewById(R.id.start_text);
        TextView end = dialogLayout.findViewById(R.id.end_text);

        start.setText("Check-in Time");
        end.setText("Check-out Time");

        final TimePicker startTime = dialogLayout.findViewById(R.id.start_time);
        final TimePicker endTime = dialogLayout.findViewById(R.id.end_time);

        builder.setTitle("Set time");
        builder.setView(dialogLayout);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //CHANGED
                        start_time = startTime.getCurrentHour() + ":" + startTime.getCurrentMinute();
                        end_time = endTime.getCurrentHour() + ":" + endTime.getCurrentMinute();
                        Log.d("prev_activity", prevActivity);
                        if (prevActivity.equals("CreateNewPlanActivity")) {
                            Event anEvent = saveEventLocally(hotel, dateFormatter2.format(checkInDate), start_time);
                            events.add(anEvent);
                            anEvent = saveEventLocally(hotel, dateFormatter2.format(checkOutDate), end_time);
                            events.add(anEvent);

                            Intent intent = new Intent(getActivity(), CreateNewPlanActivity.class);
                            intent.putParcelableArrayListExtra("events", (ArrayList<? extends Parcelable>) events);
                            getActivity().setResult(RESULT_OK, intent);
                            getActivity().finish();
                        } else {
                            //CHANGED
                            saveEventToPlan(hotel, dateFormatter2.format(checkInDate), start_time);
                            getActivity().finish();
                        }
                    }
                });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private Event saveEventLocally(Hotel hotel, String date, String time) {
        Event anEvent = new Event();
        anEvent.setQuery_id(hotel.getHotel_id());
        anEvent.setTitle(hotel.getName());
        anEvent.setLocation(hotel.getAddress());
        anEvent.setWebsite(hotel.getWebsite());
        anEvent.setDate(date);
        anEvent.setTime_start(time);
        anEvent.setTime_end(time);
        anEvent.setPhone(hotel.getPhone_number());
        anEvent.setType("hotel");
        anEvent.setRating(hotel.getRating());

        return anEvent;
    }

    private void saveEventToPlan(final Hotel hotel, String date, String time) {
        Event anEvent = new Event();
        anEvent.setQuery_id(hotel.getHotel_id());
        anEvent.setTitle(hotel.getName());
        anEvent.setLocation(hotel.getAddress());
        anEvent.setWebsite(hotel.getWebsite());
        anEvent.setDate(date);
        anEvent.setTime_start(time);
        anEvent.setTime_end(time);
        anEvent.setPhone(hotel.getPhone_number());
        anEvent.setType("hotel");

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
                        //CHANGED
                        saveEventToPlan2(hotel, dateFormatter2.format(checkOutDate), end_time);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void saveEventToPlan2(Hotel hotel, String date, String time) {
        Event anEvent = new Event();
        anEvent.setQuery_id(hotel.getHotel_id());
        anEvent.setTitle(hotel.getName());
        anEvent.setLocation(hotel.getAddress());
        anEvent.setWebsite(hotel.getWebsite());
        anEvent.setDate(date);
        anEvent.setTime_start(time);
        anEvent.setTime_end(time);
        anEvent.setPhone(hotel.getPhone_number());
        anEvent.setType("hotel");

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

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        recyclerViewPlace = getView().findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        volleyUtils = new VolleyUtils(getContext());
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        adapter = new RVAdapter_Hotel(getContext());

        progressBar = getView().findViewById(R.id.main_progress);
        connectionText = getView().findViewById(R.id.connection);

        //info
        infoView = getView().findViewById(R.id.info);
        search = getView().findViewById(R.id.search);
        sort = getView().findViewById(R.id.sort);
        infoDialog = new Dialog(getContext(), R.style.Theme_Dialog);
        infoDate = getView().findViewById(R.id.date);
        infoGuest = getView().findViewById(R.id.guest);
        infoRoom = getView().findViewById(R.id.room);

        //search
        closeSearchView = getView().findViewById(R.id.close_search_view);
        close = getView().findViewById(R.id.close);
        searchDialog = new Dialog(getContext());

        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        dateFormatter3 = new SimpleDateFormat("MMM d", Locale.US);
        dateFormatter4 = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
        try {
            checkInDateTemp = dateFormatter2.parse(getArguments().getString("date"));
            checkInDate = checkInDateTemp;

            Calendar c = Calendar.getInstance();
            c.setTime(checkInDate);
            c.add(Calendar.DATE, 1);

            checkOutDateTemp = c.getTime();
            checkOutDate = checkOutDateTemp;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        region = sessionManager.getCurrentRegion();
        regionCode = "294229";
        plan_id = getArguments().getString("plan_id");
        event_date = getArguments().getString("date");

        prevActivity = getActivity().getIntent().getStringExtra("ACTIVITY");
        if (prevActivity.equals("CreateNewPlanActivity")) {
            events = getActivity().getIntent().getParcelableArrayListExtra("events");
        }
    }

    private interface HotelDetailCallback {
        void onCallback (Hotel hotel);
    }

    private interface HotelSearchCallback {
        void onCallback ();
    }
}
