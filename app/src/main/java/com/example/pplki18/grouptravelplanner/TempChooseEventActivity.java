package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.example.pplki18.grouptravelplanner.utils.TempPagerAdapter;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class TempChooseEventActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PlaceAutocompleteFragment autocompleteFragment;
    private SessionManager sessionManager;
    private TextView textRegion;
    private LinearLayout pickDestination;
    private Bundle plan_bundle;

    private LatLng regionCoor;
    private final int REGION_AUTOCOMPLETE_REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textRegion.setText(R.string.pick_destination);
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
                                    .build(TempChooseEventActivity.this);
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
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add_white));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final TempPagerAdapter adapter = new TempPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(),
                        sessionManager.getCurrentRegion(), regionCoor, plan_bundle);

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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGION_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                sessionManager.setCurrentRegion(Objects.requireNonNull(place.getAddress()).toString());

                regionCoor = place.getLatLng();

                final TempPagerAdapter adapter = new TempPagerAdapter
                        (getSupportFragmentManager(), tabLayout.getTabCount(),
                                sessionManager.getCurrentRegion(), regionCoor, plan_bundle);

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
        }
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        textRegion = toolbar.findViewById(R.id.region);
        pickDestination = toolbar.findViewById(R.id.pick_destination);
        tabLayout = findViewById(R.id.tab_layout);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        viewPager = findViewById(R.id.pager);
        regionCoor = new LatLng(-6.17511, 106.8650395);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setCurrentRegion("Jakarta");
        plan_bundle = getIntent().getExtras();
    }
}