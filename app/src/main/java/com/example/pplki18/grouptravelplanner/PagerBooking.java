package com.example.pplki18.grouptravelplanner;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.pplki18.grouptravelplanner.utils.PagerAdapter;
import com.google.android.gms.maps.model.LatLng;

public class PagerBooking extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_booking);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter myPagerAdapter = new PagerAdapter(getSupportFragmentManager(), 5, "Jakarta", new LatLng(-6.17511, 106.8650395));
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
