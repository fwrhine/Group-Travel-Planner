package com.example.pplki18.grouptravelplanner.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.pplki18.grouptravelplanner.Fragment_EntertainmentList;
import com.example.pplki18.grouptravelplanner.Fragment_RestaurantList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String region;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String region) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.region = region;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment_RestaurantList tab1 = newInstance("restaurants");
                return tab1;
            case 1:
                Fragment_RestaurantList tab2 = newInstance("attractions");
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public Fragment_RestaurantList newInstance(String type) {
        Fragment_RestaurantList myFragment = new Fragment_RestaurantList();

        Bundle args = new Bundle();
        args.putString("TYPE", type);
        args.putString("REGION", region);
        myFragment.setArguments(args);

        return myFragment;
    }
}
