package com.example.pplki18.grouptravelplanner;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.utils.Event;
import com.example.pplki18.grouptravelplanner.utils.Hotel;
import com.example.pplki18.grouptravelplanner.utils.HtmlParser;
import com.example.pplki18.grouptravelplanner.utils.PaginationScrollListener;
import com.example.pplki18.grouptravelplanner.utils.Place;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Hotel;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.pplki18.grouptravelplanner.utils.HtmlParser.parse;

public class BookHotelFragment extends Fragment {
    private RecyclerView recyclerViewPlace;
    private LinearLayoutManager linearLayoutManager;
    private RVAdapter_Hotel adapter;
    private RequestQueue queue;
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
    private SimpleDateFormat dateFormatter5;

    private String region;
    private String regionCode;

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

        connectionText.setVisibility(View.GONE);
        closeSearchView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

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
                getTripadvisorUrl();
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);
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

    public void loadInfoView() {
        String date = dateFormatter3.format(checkInDate) + " - " + dateFormatter3.format(checkOutDate);

        infoDate.setText(date);
        infoGuest.setText(numOfGuest);
        infoRoom.setText(numOfRoom);
    }

    public void showSearchPopup() {
        searchDialog.setContentView(R.layout.search_dialog);
        SearchView searchView = (SearchView) searchDialog.findViewById(R.id.search_hotel);
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

    public void showSortPopup() {
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

    public void showInfoPopup() {
        infoDialog.setContentView(R.layout.hotel_dialog);

        // set position
        Window window = infoDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        // set animation
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // elements in infoDialog
        final TextView dialogCheckIn = (TextView) infoDialog.findViewById(R.id.start_date);
        final TextView dialogCheckOut = (TextView) infoDialog.findViewById(R.id.end_date);
        final RelativeLayout checkInLayout = (RelativeLayout) infoDialog.findViewById(R.id.check_in);
        final RelativeLayout checkOutLayout = (RelativeLayout) infoDialog.findViewById(R.id.check_out);
        final TextView dialogGuest = (TextView) infoDialog.findViewById(R.id.guest);
        final TextView dialogRoom = (TextView) infoDialog.findViewById(R.id.room);
        final Button dialogSearch = (Button) infoDialog.findViewById(R.id.search_button);
        final TextView dialogDays = (TextView) infoDialog.findViewById(R.id.days);

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

    public void getTripadvisorUrl() {
        String url = "https://www.tripadvisor.com/TypeAheadJson?action=API&startTime=" +
                System.currentTimeMillis()+ "&uiOrigin=GEOSCOPE&source=GEOSCOPE&interleaved=true" +
                "&types=geo,theme_park&neighborhood_geos=true&link_type=hotel&details=true" +
                "&max=12&injectNeighborhoods=true&query=" + region;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            JSONArray results = resp.optJSONArray("results");
                            JSONObject firstResult = new JSONObject(results.get(0).toString());
                            tripadvisorUrl = "https://www.tripadvisor.com" + firstResult.optString("url");

                            sendRequest(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        populatePlaceRecyclerView(getHotelsSearch(response));
                        adapter.removeLoadingFooter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR ALL HOTELS", error.toString());
                noConnection(error);
            }
        });

        queue.add(stringRequest);

    }

    public void sendRequest(final boolean isNextPage) {
        String url;

        if (!isNextPage) {
            url = tripadvisorUrl;
        } else {
            url = nextPageUrl;
        }


        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("changeSet", "TRAVEL_INFO");
            jsonBody.put("showSnippets", false);
            jsonBody.put("staydates", dateFormatter5.format(checkInDate) + "_" +
                    dateFormatter5.format(checkOutDate));
            jsonBody.put("uguests", numOfRoom + "_" + numOfGuest);
            jsonBody.put("sortOrder", sortBy);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            HtmlParser.HtmlParseResult result = parse(response);

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
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR HOTEL", error.toString());
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
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
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response <String> parseNetworkResponse(NetworkResponse response) {
                    String parsed;
                    try {
                        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    } catch (UnsupportedEncodingException e) {
                        parsed = new String(response.data);
                    }
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));

                }
            };

            queue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchHotel(String query) {
        String url = "https://www.tripadvisor.com/TypeAheadJson?action=API&query=" +
                query + "&interleaved=true&types=hotel&filter=nobroad&parentids=" +
                regionCode + "&name_depth=1&details=true&legacy_format=true&max=8";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("HOTEL SEARCH RESULT", response);
                        populatePlaceRecyclerView(getHotelsSearch(response));
                        adapter.removeLoadingFooter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR ALL HOTELS", error.toString());
            }
        });

        queue.add(stringRequest);
    }

//    private List<Hotel> getHotels(String response) {
//        ArrayList<Hotel> hotels = new ArrayList<>();
//        try {
//            JSONObject obj = new JSONObject(response);
//            JSONObject results = obj.optJSONObject("results");
//            JSONArray listHotels = results.optJSONArray("result");
//
//            for (int i = 0 ; i < listHotels.length() ; i++)
//            {
//                JSONObject hotelObj = new JSONObject(listHotels.get(i).toString());
//
//                Hotel hotel = new Hotel();
//                hotel.setHotel_id(hotelObj.optString("id"));
//                hotel.setName(hotelObj.optString("name"));
//                hotel.setRating(hotelObj.optString("star_rating"));
//                hotel.setAddress(hotelObj.optString("regional", "-"));
//
//                hotels.add(hotel);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//        return hotels;
//    }

    private List<Hotel> getHotelsSearch(String response) {
        ArrayList<Hotel> hotels = new ArrayList<>();
        try {
            JSONArray results = new JSONArray(response);

            for (int i = 0 ; i < results.length() ; i++)
            {
                JSONObject hotelObj = new JSONObject(results.get(i).toString());
                JSONObject hotelDetails = hotelObj.optJSONObject("details");

                Hotel hotel = new Hotel();
                hotel.setName(hotelDetails.optString("name"));
                hotel.setRating(hotelDetails.optString("star_rating", "-"));
                hotel.setAddress(hotelDetails.optString("address"));
                hotel.setWebsite(hotelObj.optString("url"));

                hotels.add(hotel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


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
        Log.d("HOTELS OBJECTS", hotels.toString());
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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        adapter = new RVAdapter_Hotel(getContext());

        progressBar = (ProgressBar) getView().findViewById(R.id.main_progress);
        connectionText = (TextView) getView().findViewById(R.id.connection);

        //info
        infoView = (ConstraintLayout) getView().findViewById(R.id.info);
        search = (ImageView) getView().findViewById(R.id.search);
        sort = (ImageView) getView().findViewById(R.id.sort);
        infoDialog = new Dialog(getContext(), R.style.Theme_Dialog);
        infoDate = (TextView) getView().findViewById(R.id.date);
        infoGuest = (TextView) getView().findViewById(R.id.guest);
        infoRoom = (TextView) getView().findViewById(R.id.room);

        //search
        closeSearchView = (ConstraintLayout) getView().findViewById(R.id.close_search_view);
        close = (ImageView) getView().findViewById(R.id.close);
        searchDialog = new Dialog(getContext());

        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        dateFormatter3 = new SimpleDateFormat("MMM d", Locale.US);
        dateFormatter4 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormatter5 = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
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
        regionCode = "294226";
    }
}
