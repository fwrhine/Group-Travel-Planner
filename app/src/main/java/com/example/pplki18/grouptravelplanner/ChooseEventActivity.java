package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.utils.PagerAdapter;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

public class ChooseEventActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    PlaceAutocompleteFragment autocompleteFragment;
    SessionManager sessionManager;
    TextView textRegion;
    LinearLayout pickDestination;
//    SearchView searchView;

    LatLng regionCoor;

    //
//    if(ContextCompat.checkSelfPermission(mActivity,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//        // Camera permission granted
//        PlaceLikelihoodBufferResponse currentPlace = PlaceDetectionClient.getCurrentPlace();

    int REGION_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);

        init();

        /*
        Set up action bar
        _________________________________________________________________________________________________
        */

        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textRegion.setText("Pick destination");
        pickDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                            .build();

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(typeFilter)
                                    .build(ChooseEventActivity.this);
                    startActivityForResult(intent, REGION_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*
        _________________________________________________________________________________________________
        */

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_restaurant));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_sunny));


        tabLayout.addTab(tabLayout.newTab().setText("Airplane"));
        tabLayout.addTab(tabLayout.newTab().setText("Train"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add_white));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), sessionManager.getCurrentRegion(), regionCoor);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("PLACE SEARCH RESULT", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR SEARCH", "An error occurred: " + status);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        sessionManager.setCurrentRegion("Jakarta");
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGION_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                sessionManager.setCurrentRegion(place.getName().toString());
//                region = place.getName().toString();
                regionCoor = place.getLatLng();

//                LatLngBounds.Builder builder = new LatLngBounds.Builder().include(place.getLatLng());
//                region_bounds = builder.build();

                final PagerAdapter adapter = new PagerAdapter
                        (getSupportFragmentManager(), tabLayout.getTabCount(), sessionManager.getCurrentRegion(), regionCoor);

                viewPager.setAdapter(adapter);
                Log.d("COOR", regionCoor.toString());

                textRegion.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("AFTER CLICK", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.d("PLACE ONE", place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("AFTER CLICK", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textRegion = (TextView) toolbar.findViewById(R.id.region);
        pickDestination = (LinearLayout)toolbar.findViewById(R.id.pick_destination);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        viewPager = (ViewPager) findViewById(R.id.pager);
//        searchView = (SearchView) findViewById(R.id.search_place);
        regionCoor = new LatLng(-6.17511, 106.8650395);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setCurrentRegion("Jakarta");

    }
}