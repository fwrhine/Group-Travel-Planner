package com.example.pplki18.grouptravelplanner;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.utils.Hotel;
import com.example.pplki18.grouptravelplanner.utils.PaginationScrollListener;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Hotel;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookHotelFragment extends Fragment {
    private RecyclerView recyclerViewPlace;
    private LinearLayoutManager linearLayoutManager;
    private ConstraintLayout infoView;
    private ConstraintLayout closeSearchView;
    private ImageView search;
    private ImageView close;
    private EditText checkIn;
    private EditText checkOut;
    private TextView textView;
    private RVAdapter_Hotel adapter;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private SessionManager sessionManager;

    private Dialog searchDialog;

    private String region;
    private String regionCode;
    private String checkInDate;
    private String checkOutDate;

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

        textView.setVisibility(View.GONE);
        closeSearchView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


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
                showPopup(getView());
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



//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String newQuery) {
////                sendRequest(newQuery + " " + type);
////                adapter.clear();
////                progressBar.setVisibility(View.VISIBLE);
//
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newQuery) {
//                if (TextUtils.isEmpty(newQuery)){
////                    sendRequest(type);
////                    adapter.clear();
////                    progressBar.set    Visibility(View.VISIBLE);
//                }
//                return false;
//            }
//        });

//        final DatePickerDialog.OnDateSetListener checkInPicker = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                // TODO Auto-generated method stub
//                checkInDate = dayOfMonth + "/" + monthOfYear + "/" + year;
//                checkIn.setText(checkInDate);
//            }
//        };
//
//        checkIn.setOnClickListener(new EditText.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                // TODO Auto-generated method stub
//                DatePickerDialog datePicker= new DatePickerDialog(getContext(), checkInPicker,
//                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH));
//
//                datePicker.show();
//                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//            }
//        });
//
//        final DatePickerDialog.OnDateSetListener checkOutPicker = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                // TODO Auto-generated method stub
//                checkOutDate = dayOfMonth + "/" + monthOfYear + "/" + year;
//                checkOut.setText(checkOutDate);
//            }
//        };
//
//        checkOut.setOnClickListener(new EditText.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                // TODO Auto-generated method stub
//                DatePickerDialog datePicker= new DatePickerDialog(getContext(), checkInPicker,
//                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH));
//
//                datePicker.show();
//                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//            }
//        });

        generateToken();

    }

    public void showPopup(View v) {
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
        String url = "https://api-sandbox.tiket.com/search/hotel?q=" + "bali" + "&startdate=2018-11-04&" +
                "night=1&enddate=2018-11-08&room=1&adult=2&token=" + token + "&output=json";

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
        infoView = (ConstraintLayout) getView().findViewById(R.id.info);
        closeSearchView = (ConstraintLayout) getView().findViewById(R.id.close_search_view);
        progressBar = (ProgressBar) getView().findViewById(R.id.main_progress);
        search = (ImageView) getView().findViewById(R.id.search);
        close = (ImageView) getView().findViewById(R.id.close);
        searchDialog = new Dialog(getContext());
//        checkIn = (EditText) getView().findViewById(R.id.check_in);
//        checkOut = (EditText) getView().findViewById(R.id.check_out);
        textView = (TextView) getView().findViewById(R.id.connection);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        adapter = new RVAdapter_Hotel(getContext());
        region = sessionManager.getCurrentRegion();
        regionCode = "294226";
    }
}
