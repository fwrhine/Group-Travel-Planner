package com.example.pplki18.grouptravelplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.data.Hotel;
import com.example.pplki18.grouptravelplanner.utils.PaginationScrollListener;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Hotel;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
//                loadMorePlaces();
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
                generateToken();
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

        generateToken();

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
                    generateToken();
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
                generateToken();
            }
        });

        infoDialog.show();
    }

    public void generateToken() {
        String secretKey = "6c484049beacda6541bf40c90e62e8e5";
        String url = "https://api-sandbox.tiket.com/apiv1/payexpress"
                + "?method=getToken&secretkey=" + secretKey + "&output=json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            String token = json.getString("token");

                            Log.d("TOKEN HOTEL", token);


                            sendRequest(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR HOTEL", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    public void sendRequest(String token) {
        String startdate = dateFormatter4.format(checkInDate);
        String enddate = dateFormatter4.format(checkOutDate);
        String url = "https://api-sandbox.tiket.com/search/hotel?q=" + region + "&startdate=" +
                startdate + "&night=" + nights + "&enddate=" + enddate + "&room=" + numOfRoom +
                "&adult=" + numOfGuest + "&token=" + token + "&output=json";

        Log.d("HOTEL REQUEST", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("HOTELS", response);
                        populatePlaceRecyclerView(getHotels(response));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR ALL HOTELS", error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
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

    private List<Hotel> getHotels(String response) {
        ArrayList<Hotel> hotels = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject results = obj.optJSONObject("results");
            JSONArray listHotels = results.optJSONArray("result");

            for (int i = 0 ; i < listHotels.length() ; i++)
            {
                JSONObject hotelObj = new JSONObject(listHotels.get(i).toString());

                Hotel hotel = new Hotel();
                hotel.setHotel_id(hotelObj.optString("id"));
                hotel.setName(hotelObj.optString("name"));
                hotel.setRating(hotelObj.optString("star_rating"));
                hotel.setAddress(hotelObj.optString("regional", "-"));

                hotels.add(hotel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return hotels;
    }

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
